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
                    (
                        music.composerEng ILIKE CONCAT('%', :composer, '%')
                        OR
                        music.composerKor ILIKE CONCAT('%', :composer, '%')
                    )
                    AND
                    (
                        music.nameEng ILIKE CONCAT('%', :name, '%')
                        OR
                        music.nameKor ILIKE CONCAT('%', :name, '%')
                    )
        """,
    )
    fun getMusicByFilterString(
        @Param("composer")
        composer: String,
        @Param("name")
        name: String,
    ): List<Music>

    fun findAllByIdIn(id: List<Long>): List<Music>
}
