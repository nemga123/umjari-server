package com.umjari.server.domain.music.repository

import com.umjari.server.domain.music.model.Music
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MusicRepository : JpaRepository<Music, Long?> {
    fun getByComposerEngAndComposerKorAndNameEngAndNameKor(
        composerEng: String,
        composerKor: String,
        nameEng: String,
        nameKor: String,
    ): Music?

    @Query(
        """
            SELECT music FROM Music AS music
                WHERE
                    music.composerEng ILIKE CONCAT('%', :composerEng, '%')
                    AND music.composerKor ILIKE CONCAT('%', :composerKor, '%')
                    AND music.nameEng ILIKE CONCAT('%', :nameEng, '%')
                    AND music.nameKor ILIKE CONCAT('%', :nameKor, '%')
        """,
    )
    fun getMusicByFilterString(
        @Param("composerEng")
        composerEng: String,
        @Param("composerKor")
        composerKor: String,
        @Param("nameEng")
        nameEng: String,
        @Param("nameKor")
        nameKor: String,
    ): List<Music>
}
