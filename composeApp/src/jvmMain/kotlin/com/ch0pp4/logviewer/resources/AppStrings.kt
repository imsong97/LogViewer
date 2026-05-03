package com.ch0pp4.logviewer.resources

object AppStrings {
    // app title
    const val APP_TITLE = "LogViewer"

    // drop box
    const val DROPBOX_PLACEHOLDER = ".txt / .log 파일을 이곳에 드래그 하세요"
    const val DROPBOX_UNSUPPORTED_FILE = ".txt / .log 파일만 지원합니다."

    // search layout
    const val SEARCH_LABEL = "Search"
    const val SEARCH_PLACEHOLDER = ""
    const val SEARCH_FILTER_ON = "필터 ON"
    const val SEARCH_FILTER_OFF = "필터 OFF"
    const val SEARCH_TAG_LABEL = "Tag"
    const val SEARCH_TAG_PLACEHOLDER = ""
    const val SEARCH_TAG_FILTER_ON =  "필터 ON"
    const val SEARCH_TAG_FILTER_OFF =  "필터 OFF"
    const val FILTER_LOG_LEVEL = "LogLV"
    const val FILTER_LOG_LEVEL_RESET = "초기화"

    const val LOG_LEVEL_D = "D"
    const val LOG_LEVEL_E = "E"
    const val LOG_LEVEL_W = "W"

    const val FILTER_BOOKMARK = "북마크만"

    const val HIDE_UNPARSED_LOGS = "파싱 불가 숨김"

    // file flow layout
    const val FILE_LIST_LABEL = "파일: "
    const val FILE_REMOVE = "X"
    const val FILE_REMOVE_ALL = "전체 삭제"
    const val FILE_ADD_BTN = "+ 추가"

    // header column
    const val HEADER_FILE = "File"
    const val HEADER_DATE = "Date"
    const val HEADER_TIME = "Time"
    const val HEADER_LOG_LEVEL = "LogLV"
    const val HEADER_THREAD = "Thread"
    const val HEADER_TAG = "Tag"
    const val HEADER_DESCRIPTION = "Description"
}