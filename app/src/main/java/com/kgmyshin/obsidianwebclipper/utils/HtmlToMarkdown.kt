package com.kgmyshin.obsidianwebclipper.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun htmlToMarkdown(html: String): String {
    val doc = Jsoup.parse(html)

    // 不要な要素を削除
    doc.select("header, footer, nav, script, style, aside, noscript").remove()

    val builder = StringBuilder()

    for (element in doc.body().children()) {
        builder.append(processElementRecursively(element)).append("\n\n")
    }

    return builder.toString().trim()
}

fun processElementRecursively(element: Element): String {
    return when (element.tagName()) {
        "h1" -> "# ${renderChildren(element)}\n\n"
        "h2" -> "## ${renderChildren(element)}\n\n"
        "h3" -> "### ${renderChildren(element)}\n\n"
        "h4" -> "#### ${renderChildren(element)}\n\n"
        "h5" -> "##### ${renderChildren(element)}\n\n"
        "h6" -> "###### ${renderChildren(element)}\n\n"
        "p" -> "${renderChildren(element)}\n\n"
        "br" -> "\n"
        "strong", "b" -> "**${renderChildren(element)}**"
        "em", "i" -> "*${renderChildren(element)}*"
        "a" -> "[${renderChildren(element)}](${element.attr("href")})"
        "img" -> "![" + element.attr("alt") + "](" + element.attr("src") + ")"
        "code" -> "`" + renderChildren(element) + "`"
        "pre" -> "```\n${element.text()}\n```\n\n"
        "blockquote" -> "> ${renderChildren(element)}\n\n"
        "ul" -> element.children().joinToString("") { "- ${renderChildren(it)}\n" } + "\n"
        "ol" -> element.children().mapIndexed { i, li -> "${i + 1}. ${renderChildren(li)}" }
            .joinToString("\n") + "\n\n"

        "li" -> renderChildren(element) // <li> は親が ul/ol なのでここでは改行不要
        else -> renderChildren(element)
    }
}


// 子ノードをたどって Markdown に再構成する
fun renderChildren(parent: Element): String {
    val builder = StringBuilder()
    for (node in parent.childNodes()) {
        when (node) {
            is Element -> builder.append(processElementRecursively(node))
            is org.jsoup.nodes.TextNode -> builder.append(node.text())
        }
    }
    return builder.toString()
}
