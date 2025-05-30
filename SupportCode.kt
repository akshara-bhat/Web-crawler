import java.io.File
import java.lang.AssertionError
import java.net.URI

private class EllenError(
    message: String? = null,
) : AssertionError(
        "Please report this error to Ellen" +
            if (message != null) ": $message" else "",
    )


const val CRAWL_BASE_URL = "https://learnodo-newtonic.com/famous-american-poems"

/**
 * Simulates a web server by prompting users for queries and displaying results.
 */
fun simulateWebServer() {
    val crawler = Crawler()
    crawler.crawl(URI(CRAWL_BASE_URL))
    val server = crawler.buildServer()
    while (true) {
        println("Enter your search query (or /quit to quit): ")
        val searchTerms = readln()
        if (searchTerms == "/quit") {
            return
        }
        val results = server.getSearchResults(searchTerms)
        println(
            if (results.size == 1) {
                "1 result was found:"
            } else {
                "${results.size} results were found:"
            },
        )
        println(
            results.joinToString(
                separator = "\n\n",
                prefix = "\n",
                postfix = "\n",
            ),
        )
    }
}

/**
 * A simulated network connection.
 */
class Network {
    /**
     * Returns the page with the given [uri] from a simulated network, or
     * returns null if the page cannot be retrieved.
     */
    fun fetch(uri: URI): WebPage? = web[uri]

    private enum class State {
        Begin,
        Title,
        Body,
        Links,
    }

    companion object {
        private val web = mutableMapOf<URI, WebPage>()

        init {
            val sentinel = "END"
            var state = State.Begin
            var uri: URI? = null
            var title: String? = null
            val body = StringBuilder()
            var page: WebPage? = null
            File("src/web.txt").forEachLine {
                if (it.trim().isEmpty()) {
                    // do nothing
                } else if (state == State.Begin) {
                    assertFalse(it == sentinel)
                    uri = URI(it)
                    state = State.Title
                } else if (state == State.Title) {
                    title = it
                    state = State.Body
                } else if ((state == State.Body || state == State.Links) && it.startsWith(sentinel)) {
                    if (page == null) {
                        page = WebPage(uri ?: throw EllenError(), title ?: throw EllenError(), body.toString())
                        body.clear()
                    }
                    if (uri == null || page == null) {
                        throw EllenError()
                    }
                    uri?.let { u ->
                        web[u] = page ?: throw EllenError()
                        page = null
                        uri = null
                        state = State.Begin
                    }
                } else if (it.startsWith("http")) {
                    assertTrue(state == State.Body || state == State.Links)
                    if (state == State.Body || page == null) {
                        page = WebPage(uri ?: throw EllenError(), title ?: throw EllenError(), body.toString())
                        body.clear()
                        state = State.Links
                    }
                    page?.links?.add(URI(it)) ?: throw EllenError()
                } else if (state == State.Body) {
                    body.append(it).append("\n")
                } else {
                    throw EllenError("Unreachable code; state: $state, it: $it")
                }
            }
            assertEquals(State.Begin, state)
        }

        fun describeNetwork() {
            for (value in web.values) {
                println(value.uri)
                println(value.title)
                println()
            }
        }
    }
}

/**
 * Verifies that [actual] is equal to [expected].
 *
 * @throws AssertionError if they are not equal
 */
fun assertEquals(
    expected: Any?,
    actual: Any?,
) {
    if (expected != actual) {
        throw AssertionError("Expected $expected, got $actual")
    }
}

/**
 * Verifies that [actual] is true.
 *
 * @throws AssertionError if it is false
 */
fun assertTrue(actual: Any?) {
    assertEquals(true, actual)
}

/**
 * Verifies that [actual] is false.
 *
 * @throws AssertionError if it is true
 */
fun assertFalse(actual: Any?) {
    assertEquals(false, actual)
}

/**
 * Asserts that the [actual] value is not `null`.
 *
 * @throws AssertionError if it is null
 */
fun assertNotNull(actual: Any?) {
    if (actual == null) {
        throw AssertionError("Unexpected null value")
    }
}
