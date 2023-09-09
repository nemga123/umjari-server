package com.umjari.server.domain.concert.specification

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.region.model.Region
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.util.Date

class ConcertSpecificationBuilder {
    private var spec = Specification<Concert> { root, query, _ ->
        if (Concert::class.java == query.resultType) {
            root.fetch<Concert, ConcertMusic>("playList", JoinType.LEFT)
                .fetch<ConcertMusic, Music>("music", JoinType.LEFT)
            root.fetch<Concert, Region>("region")
        }
        null
    }

    fun filteredByDateStart(dateStart: Date) {
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(
                root.get("concertDate"),
                dateStart,
            )
        }
    }

    fun filteredByDateEnd(dateEnd: Date) {
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.lessThanOrEqualTo(
                root.get("concertDate"),
                dateEnd,
            )
        }
    }

    fun filteredByRegionParent(regionParent: String) {
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<Region>("region").get<String>("parent"),
                regionParent,
            )
        }
    }

    fun filteredByRegionChild(regionChild: String) {
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<Region>("region").get<String>("child"),
                regionChild,
            )
        }
    }

    fun filteredByComposer(composer: String) {
        val composerSpec = Specification<Concert> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("composerKor")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortComposerKor")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("composerEng")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortComposerEng")),
                "%${composer.uppercase()}%",
            )
        }

        spec = spec.and(composerSpec)
    }

    fun filteredByMusicName(musicName: String) {
        val musicNameSpec = Specification<Concert> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("nameEng")),
                "%${musicName.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("nameKor")),
                "%${musicName.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortNameEng")),
                "%${musicName.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortNameKor")),
                "%${musicName.uppercase()}%",
            )
        }

        spec = spec.and(musicNameSpec)
    }

    fun filteredByText(text: String) {
        val textSpec = Specification<Concert> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("title")),
                "%${text.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("subtitle")),
                "%${text.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("concertInfo")),
                "%${text.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("regionDetail")),
                "%${text.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<Group>("group").get("name")),
                "%${text.uppercase()}%",
            )
        }

        spec = spec.and(textSpec)
    }

    fun build(): Specification<Concert> {
        return spec
    }
}
