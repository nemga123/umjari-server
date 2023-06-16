package com.umjari.server.domain.concert.specification

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.region.model.Region
import org.springframework.data.jpa.domain.Specification
import java.util.Date

class ConcertSpecification {
    private var spec = Specification<Concert> { root, query, _ ->
        if (Concert::class.java == query.resultType) {
            root.fetch<Concert, ConcertMusic>("playList")
                .fetch<ConcertMusic, Music>("music")
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

    fun filteredByText(text: String) {
        var textSpec = Specification<Concert> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("title")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("subtitle")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("concertInfo")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("nameEng")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("nameKor")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortNameEng")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<ConcertMusic>("playList").get<Music>("music").get("shortNameKor")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("regionDetail")),
                "%${text.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
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
