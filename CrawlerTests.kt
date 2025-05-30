import java.net.URI

val khouryTestPage =
    WebPage(
        URI("https://www.khoury.northeastern.edu/about/history/"),
        "Khoury History",
        "In the early 1980s, Northeastern University created the nation’s first college dedicated to the field of computer science (CS). Today, Khoury College of Computer Sciences remains a national leader in CS education and research—continuing to break new ground to solve real-world problems.",
    )

val acmTestPage = WebPage(
    URI("https://www.acm.org/about-acm/about-the-acm-organization"),
    "About ACM",
    "ACM brings together computing educators, researchers, and professionals to inspire dialogue, share resources, and address the field's challenges. As the world’s largest computing society, ACM strengthens the profession's collective voice through strong leadership, " +
            "promotion of the highest standards, and recognition of technical excellence. ACM supports the professional growth of its members by providing opportunities for life‐long learning, career development, and professional networking.\n" +
            "Founded at the dawn of the computer age, ACM’s reach extends to every part of the globe, with more than half of its more than 100,000 members residing outside the U.S.  Its growing membership has led to Councils in Europe, India, and China, fostering networking " +
            "opportunities that strengthen ties within and across countries and technical communities.  Their actions enhance ACM’s ability to raise awareness of computing’s important technical, educational, and social issues around the world."
)

fun testAddToIndex() {
    // Create a crawler and add these two pages to its index.
    val testCrawler = Crawler()
    testCrawler.addToIndex(khouryTestPage)
    testCrawler.addToIndex(acmTestPage)

    // The pages do NOT contain the word "canada", so make sure it's not in the
    // index.
    assertFalse(testCrawler.index.containsKey("canada"))

    // Exactly one of the pages (acmTestPage) contains the word "acm", so there
    // should be an entry in the index with the key "acm". The associated value
    // should be a one-element list with the matching web page.
    assertTrue(testCrawler.index.containsKey("acm"))
    val pagesWithAcm = testCrawler.index["acm"]
    assertNotNull(pagesWithAcm)
    assertEquals(1, pagesWithAcm?.size) // there is 1 page with it
    assertEquals(acmTestPage, pagesWithAcm?.get(0)) // it's the ACM page

    // Both pages (acmTestPage and khouryTestPage) contain the word "computer",
    // so there should be an entry in the index with the key "computer".
    // The associated value should be a list with the two web pages (in either
    // order).
    assertTrue(testCrawler.index.containsKey("computer")) // computer should be in the index
    val pagesWithComputing = testCrawler.index["computer"]
    assertNotNull(pagesWithAcm)
    assertEquals(2, pagesWithComputing?.size) // there are 2 pages with it
    if (pagesWithComputing != null) {
        assertTrue(pagesWithComputing.contains(acmTestPage)) // one should be the ACM page
        assertTrue(pagesWithComputing.contains(khouryTestPage)) // the other should be Khoury
    }
}

const val ED_POEM_479_URL =
    "https://www.poetryfoundation.org/poems/47652/because-i-could-not-stop-for-death-479"
const val ED_BIO_URL = "https://www.poetryfoundation.org/poets/emily-dickinson"

fun testCrawl() {
    val testCrawler = Crawler()
    assertEquals(0, testCrawler.visited.size)

    // Crawl a page (ED_POEM_479_URL) that links to another page (ED_BIO_URL).
    testCrawler.crawl(URI(ED_POEM_479_URL))

    // Check that both pages have been visited.
    assertEquals(2, testCrawler.visited.size)
    assertTrue(testCrawler.visited.contains(URI(ED_POEM_479_URL)))
    assertTrue(testCrawler.visited.contains(URI(ED_BIO_URL)))
}

fun testMakeSnippet1() {
    // Make a snippet containing "computer" for the Khoury page.
    val snippet = khouryTestPage.makeSnippet(listOf("computer"))

    // Check that the snippet's length is appropriate and that it contains
    // the search term.
    assertTrue(snippet.length <= MAX_SNIPPET_LENGTH)
    assertTrue(snippet.contains("computer", ignoreCase = true))
}

fun testMakeSnippet2() {
    // Make a snippet containing "northeastern" for the Khoury page.
    val snippet = khouryTestPage.makeSnippet(listOf("northeastern"))

    // Check that the snippet's length is appropriate and that it contains
    // the word in its original case.
    assertTrue(snippet.length <= MAX_SNIPPET_LENGTH)
    assertTrue(snippet.contains("Northeastern"))
}

fun testMakeSnippet3() {
    // Create a snippet that contains "college" or "Khoury" (or both).
    val snippet = khouryTestPage.makeSnippet(listOf("college", "khoury"))

    // Check that the snippet's length is appropriate and that it contains at
    // least one of the two words in its original case.
    assertTrue(snippet.length <= MAX_SNIPPET_LENGTH)
    assertTrue(
        snippet.contains("college") || snippet.contains("Khoury"),
    )
}

fun testMakeSnippet() {
    testMakeSnippet1()
    testMakeSnippet2()
    testMakeSnippet3()
}

