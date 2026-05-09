package com.ch0pp4.logviewer.resources

object AppStrings {
    // app title
    const val APP_TITLE = "LogViewer"

    // common
    const val COMMON_FILE_FORMAT_LOG = ".log"
    const val COMMON_FILE_FORMAT_TXT = ".txt"
    const val COMMON_UNSUPPORTED_FILE_MESSAGE = "$COMMON_FILE_FORMAT_TXT / $COMMON_FILE_FORMAT_LOG 파일만 지원합니다."
    const val COMMON_LOG_LEVEL_D = "D"
    const val COMMON_LOG_LEVEL_E = "E"
    const val COMMON_LOG_LEVEL_W = "W"
    const val COMMON_DIALOG_BTN_CANCEL = "취소"

    // drop box
    const val DROPBOX_PLACEHOLDER_ICON = "📄"
    const val DROPBOX_PLACEHOLDER = "$COMMON_FILE_FORMAT_TXT / $COMMON_FILE_FORMAT_LOG 파일을 이곳에 드래그 하세요"

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

    const val FILTER_BOOKMARK = "북마크만"

    const val HIDE_UNPARSED_LOGS = "파싱 불가 숨김"

    // file list layout
    const val FILE_LIST_LABEL = "파일: "
    const val FILE_REMOVE = "X"
    const val FILE_REMOVE_ALL = "전체 삭제"
    const val FILE_ADD_BTN = "+ 추가" // "⚙ 설정"
    const val FILE_REMOVE_DIALOG_MESSAGE = "로드된 파일 %d개를 모두 삭제하시겠습니까?"
    const val FILE_REMOVE_DIALOG_BTN_DELETE = "삭제"

    // header column
    const val HEADER_FILE = "File"
    const val HEADER_DATE = "Date"
    const val HEADER_TIME = "Time"
    const val HEADER_LOG_LEVEL = "LogLV"
    const val HEADER_THREAD = "Thread"
    const val HEADER_TAG = "Tag"
    const val HEADER_DESCRIPTION = "Description"

    // file browser
    const val FILE_BROWSER_TITLE = "파일 선택"
    const val FILE_BROWSER_NOT_SUPPORTED_TITLE = "지원하지 않는 파일 형식"

    // menuBar
    const val MENU_SETTING_TITLE = "설정"
    const val MENU_ITEM_USER_SETTINGS = "사용자 설정"
}