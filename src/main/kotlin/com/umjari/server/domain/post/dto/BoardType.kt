package com.umjari.server.domain.post.dto

import com.umjari.server.domain.post.exception.BoardNameNotFoundException
import java.lang.IllegalArgumentException

enum class BoardType(
    val boardType: String,
) {
    FREE("자유게시판"),
    VIOLIN("바이올린"),
    FIRST_VIOLIN("퍼스트 바이올린"),
    SECOND_VIOLIN("세컨드 바이올린"),
    VIOLA("비올라"),
    CELLO("첼로"),
    BASS("베이스"),
    FLUTE("플루트"),
    PICCOLO("피콜로"),
    CLARINET("클라리넷"),
    A_CLARINET("A클라리넷"),
    E_CLARINET("E클라리넷"),
    BASS_CLARINET("베이스클라리넷"),
    OBOE("오보에"),
    ENGLISH_HORN("잉혼"),
    BASSOON("바순"),
    CONTRABASSOON("콘트라바순"),
    HORN("호른"),
    TRUMPET("트럼펫"),
    CORNET("코넷"),
    TROMBONE("트롬본"),
    BASS_TROMBONE("베이스트롬본"),
    TUBA("튜바"),
    PERCUSSION_INSTRUMENT("타악기"),
    OTHERS("기타 악기"),
    ;

    companion object {
        fun boardNameToBoardType(boardName: String): BoardType {
            try {
                return BoardType.valueOf(boardName.uppercase())
            } catch (e: IllegalArgumentException) {
                throw BoardNameNotFoundException(boardName.uppercase())
            }
        }
    }
}
