package com.umjari.server.domain.group.specification

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.model.GroupMusic
import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.region.model.Region
import org.springframework.data.jpa.domain.Specification

class GroupSpecification {
    private var spec = Specification<Group> { root, query, _ ->
        if (Group::class.java == query.resultType) {
            root.fetch<Group, GroupMusic>("setList")
                .fetch<ConcertMusic, Music>("music")
            root.fetch<Concert, Region>("region")
        }
        null
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
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("name")),
                "%${text.uppercase()}%",
            )
        }
    }

    fun filteredByComposer(composer: String) {
        var textSpec = Specification<Group> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("composerKor")),
                "%${composer.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortComposerKor")),
                "%${composer.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("composerEng")),
                "%${composer.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortComposerEng")),
                "%${composer.uppercase()}%",
            )
        }

        spec = spec.and(textSpec)
    }

    fun filteredByMusicName(name: String) {
        var textSpec = Specification<Group> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("nameKor")),
                "%${name.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortNameKor")),
                "%${name.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("nameEng")),
                "%${name.uppercase()}%",
            )
        }
        textSpec = textSpec.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortNameEng")),
                "%${name.uppercase()}%",
            )
        }

        spec = spec.and(textSpec)
    }

    fun filteredByRecruitInstruments(recruit: List<Instrument>) {
        val instrumentsSpec = Specification<Group> { _, query, criteriaBuilder ->
            val sub = query.subquery(Long::class.java)
            val subRoot = sub.from(Group::class.java)
            val subPredicate = criteriaBuilder.`in`(subRoot.get<Instrument>("recruitInstruments"))
            for (inst in recruit) {
                subPredicate.value(inst)
            }
            sub.select(subRoot.get("id")).where(subPredicate)
            sub.groupBy(subRoot.get<Long>("id"))
            sub.having(criteriaBuilder.equal(criteriaBuilder.count(subRoot.get<Long>("id")), recruit.size.toLong()))

            criteriaBuilder.`in`(subRoot.get<Long>("id")).value(sub)
        }
        spec = spec.and(instrumentsSpec)
    }

    fun build(): Specification<Group> {
        return spec
    }
}
