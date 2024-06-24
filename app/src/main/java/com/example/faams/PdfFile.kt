package com.example.faams

data class PdfFile(
    val fileName: String= "",
    var downloadUrl: String = "",
    var key: String = ""

) {
    constructor() : this("", "", "")
}
