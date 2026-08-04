package eu.kanade.tachiyomi.extension.ko.kmana

import android.app.Application
import android.content.SharedPreferences
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.regex.Pattern

class KMana : HttpSource() {
    override val name = "K만화 (KMana)"
    override val lang = "ko"
    override val supportsLatest = true

    override val baseUrl: String by lazy {
        preferences.getString(BASE_URL_PREF, defaultBaseUrl) ?: defaultBaseUrl
    }
    private val defaultBaseUrl = "https://kmana10.net"

    private val preferences: SharedPreferences by lazy {
        Injekt.get<Application>().getSharedPreferences("source_$id", 0x0000)
    }

    override fun headersBuilder(): Headers.Builder = super.headersBuilder()
        .add("Referer", baseUrl)
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")

    // ============================== Popular ===============================
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/today100?page=$page", headers)

    override fun popularMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()
        val mangas = document.select(".mSearchListContainer a.item, .item_grid a.item").map { element ->
            SManga.create().apply {
                title = element.select("h3").text().trim()
                url = element.attr("href")
                thumbnail_url = element.select(".mLazyImgPlaceholder, img").let {
                    it.attr("data-src").ifEmpty { it.attr("src") }
                }.let {
                    if (it.startsWith("//")) "https:$it" else it
                }
            }
        }
        val hasNextPage = document.select(".m-pagination a.next").isNotEmpty()
        return MangasPage(mangas, hasNextPage)
    }

    // =============================== Latest ===============================
    override fun latestUpdatesRequest(page: Int): Request =
        GET("$baseUrl/latest?page=$page", headers)

    override fun latestUpdatesParse(response: Response): MangasPage {
        return popularMangaParse(response) // reusing logic since selector is same
    }

    // =============================== Search ===============================
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request =
        GET("$baseUrl/search?key=$query&page=$page", headers)

    override fun searchMangaParse(response: Response): MangasPage {
        return popularMangaParse(response) // reusing logic since selector is same
    }

    // =========================== Manga Details ============================
    override fun mangaDetailsParse(response: Response): SManga {
        val document = response.asJsoup()
        return SManga.create().apply {
            title = document.select(".m-detail-viewbox-tit h1").text().trim()
            thumbnail_url = document.select(".mToonImageContainer img, .viewinfo img").firstOrNull()?.let {
                val url = it.attr("data-src").ifEmpty { it.attr("src") }
                if (url.startsWith("//")) "https:$url" else url
            }
        }
    }

    // ============================== Chapters ==============================
    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        return document.select("a.mEpisodeItem").map { element ->
            SChapter.create().apply {
                name = element.select("h5").text().trim()
                url = element.attr("href")
                val dateStr = element.select(".view_date_item").firstOrNull()?.text()
            }
        }
    }

    // =============================== Pages ================================
    override fun pageListParse(response: Response): List<Page> {
        val document = response.asJsoup()
        val html = document.html()

        val folderMatcher = Pattern.compile("const folder = \"([^\"]+)\"").matcher(html)
        val folder2Matcher = Pattern.compile("const folder2 = \"([^\"]+)\"").matcher(html)
        val urlsMatcher = Pattern.compile("const urls = \"([^\"]+)\"").matcher(html)

        val folder = if (folderMatcher.find()) folderMatcher.group(1) else ""
        val folder2 = if (folder2Matcher.find()) folder2Matcher.group(1) else ""
        val urlsStr = if (urlsMatcher.find()) urlsMatcher.group(1) else ""

        val imageCdnDomain = "https://smallimage.11toon8.com/data/toon"

        if (urlsStr.isNullOrEmpty()) {
            throw Exception("Failed to find image URLs in the page script.")
        }

        return urlsStr.split(",").mapIndexed { i, imgName ->
            val imgUrl = "$imageCdnDomain/$folder/$folder2/$imgName"
            Page(i, "", imgUrl)
        }
    }

    override fun imageUrlParse(response: Response): String = ""

    companion object {
        private const val BASE_URL_PREF = "overrideBaseUrl"
    }
}
