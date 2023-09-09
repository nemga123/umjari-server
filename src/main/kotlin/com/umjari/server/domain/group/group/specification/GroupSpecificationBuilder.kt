package com.umjari.server.domain.group.group.specification

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.group.groupmusics.model.GroupMusic
import com.umjari.server.domain.group.instruments.Instrument
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.region.model.Region
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification

class GroupSpecificationBuilder {
    private var spec = Specification<Group> { root, query, _ ->
        if (Group::class.java == query.resultType) {
            root.fetch<Group, Instrument>("recruitInstruments", JoinType.LEFT)
            root.fetch<Group, Region>("region")
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

    fun filteredByName(name: String) {
        spec = spec.and { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("name")),
                "%${name.uppercase()}%",
            )
        }
    }

    fun filteredByComposer(composer: String) {
        val textSpec = Specification<Group> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("composerKor")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortComposerKor")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("composerEng")),
                "%${composer.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
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
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortNameKor")),
                "%${name.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("nameEng")),
                "%${name.uppercase()}%",
            )
        }.or { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<GroupMusic>("setList").get<Music>("music").get("shortNameEng")),
                "%${name.uppercase()}%",
            )
        }

        spec = spec.and(textSpec)
    }

    fun filteredByRecruitInstruments(recruit: List<Instrument>) {
        val instrumentsSpec = Specification<Group> { root, query, criteriaBuilder ->
            val sub = query.subquery(Long::class.java)
            val subRoot = sub.from(Group::class.java)
            val subPredicate = criteriaBuilder.`in`(subRoot.join<Group, Instrument>("recruitInstruments"))
            for (inst in recruit) {
                subPredicate.value(inst)
            }
            sub.select(subRoot.get("id")).where(subPredicate)
            sub.groupBy(subRoot.get<Long>("id"))
            sub.having(criteriaBuilder.equal(criteriaBuilder.count(subRoot.get<Long>("id")), recruit.size.toLong()))

            criteriaBuilder.`in`(root.get<Long>("id")).value(sub)
        }
        spec = spec.and(instrumentsSpec)
    }

    fun filteredByTags(tags: List<String>) {
        var tagSpec = Specification<Group> { _, _, _ -> null }
        for (tag in tags) {
            tagSpec = tagSpec.and { root, _, criteriaBuilder ->
                criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("tags")),
                    "%,${tag.uppercase()},%",
                )
            }
        }
        spec = spec.and(tagSpec)
    }

    fun build(): Specification<Group> {
        return spec
    }
}
