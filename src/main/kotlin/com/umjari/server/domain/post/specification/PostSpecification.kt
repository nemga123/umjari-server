package com.umjari.server.domain.post.specification

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.exception.FilterTypeNotFoundException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.model.CommunityPostReply
import com.umjari.server.domain.user.model.User
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.lang.IllegalArgumentException

class PostSpecification {
    private var spec: Specification<CommunityPost>
    constructor() {
        spec = Specification<CommunityPost> { root, query, _ ->
            if (CommunityPost::class.java == query.resultType) {
                root.fetch<CommunityPost, CommunityPostReply>("replies", JoinType.LEFT)
                root.fetch<CommunityPost, User>("author")
            }
            null
        }
    }

    constructor(boardName: BoardType) {
        spec = Specification<CommunityPost> { root, query, criteriaBuilder ->
            if (CommunityPost::class.java == query.resultType) {
                root.fetch<CommunityPost, CommunityPostReply>("replies", JoinType.LEFT)
                root.fetch<CommunityPost, User>("author")
            }
            criteriaBuilder.equal(root.get<BoardType>("board"), boardName)
        }
    }

    private fun filteredByTitle(text: String): Specification<CommunityPost> {
        return Specification<CommunityPost> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("title")),
                "%${text.uppercase()}%",
            )
        }
    }

    private fun filteredByContent(text: String): Specification<CommunityPost> {
        return Specification<CommunityPost> { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get("content")),
                "%${text.uppercase()}%",
            )
        }
    }

    private fun filteredByAuthor(text: String): Specification<CommunityPost> {
        val anonymousPostSpec = Specification<CommunityPost> { root, _, criteriaBuilder ->
            val p1 = criteriaBuilder.equal(root.get<Boolean>("isAnonymous"), true)
            val p2 = criteriaBuilder.like(
                criteriaBuilder.upper(root.get("authorNickname")),
                "%${text.uppercase()}%",
            )
            criteriaBuilder.and(p1, p2)
        }

        val notAnonymousPostSpec = Specification<CommunityPost> { root, _, criteriaBuilder ->
            val p1 = criteriaBuilder.equal(root.get<Boolean>("isAnonymous"), false)
            val p2 = criteriaBuilder.like(
                criteriaBuilder.upper(root.get<User>("author").get("profileName")),
                "%${text.uppercase()}%",
            )
            criteriaBuilder.and(p1, p2)
        }

        return anonymousPostSpec.or(notAnonymousPostSpec)
    }

    private fun filteredByReplyContent(text: String): Specification<CommunityPost> {
        return Specification<CommunityPost> { root, query, criteriaBuilder ->
            val sub = query.subquery(Long::class.java)
            val subRoot = sub.from(CommunityPostReply::class.java)
            val subPredicate = criteriaBuilder.like(
                criteriaBuilder.upper(subRoot.get("content")),
                "%$text%",
            )
            sub.select(subRoot.get<CommunityPost>("post").get("id")).where(subPredicate)
            criteriaBuilder.`in`(root.get<Long>("id")).value(sub)
        }
    }

    private fun filteredByReplyAuthor(text: String): Specification<CommunityPost> {
        val anonymousReplySpec: (Root<CommunityPostReply>, CriteriaBuilder) -> Predicate = { root, criteriaBuilder ->
            val p1 = criteriaBuilder.equal(root.get<Boolean>("isAnonymous"), true)
            val p2 = criteriaBuilder.like(
                criteriaBuilder.upper(root.get("authorNickname")),
                "%${text.uppercase()}%",
            )
            criteriaBuilder.and(p1, p2)
        }

        val notAnonymousReplySpec: (Root<CommunityPostReply>, CriteriaBuilder) -> Predicate = { root, criteriaBuilder ->
            val p1 = criteriaBuilder.equal(root.get<Boolean>("isAnonymous"), false)
            val p2 = criteriaBuilder.like(
                criteriaBuilder.upper(root.get<User>("author").get("profileName")),
                "%${text.uppercase()}%",
            )
            criteriaBuilder.and(p1, p2)
        }

        return Specification<CommunityPost> { root, query, criteriaBuilder ->
            val sub = query.subquery(Long::class.java)
            val subRoot = sub.from(CommunityPostReply::class.java)
            val subPredicate = criteriaBuilder.or(
                anonymousReplySpec(subRoot, criteriaBuilder),
                notAnonymousReplySpec(subRoot, criteriaBuilder),
            )
            sub.select(subRoot.get<CommunityPost>("post").get("id")).where(subPredicate)
            criteriaBuilder.`in`(root.get<Long>("id")).value(sub)
        }
    }

    private fun filteredByAll(text: String): Specification<CommunityPost> =
        filteredByAuthor(text)
            .or(filteredByTitle(text))
            .or(filteredByContent(text))
            .or(filteredByReplyAuthor(text))
            .or(filteredByReplyContent(text))
            .or { _, query, _ ->
                query.distinct(true)
                null
            }

    fun build(filterType: FilterType, text: String): Specification<CommunityPost> {
        return when (filterType) {
            FilterType.ALL -> spec.and(filteredByAll(text))
            FilterType.TITLE -> spec.and(filteredByTitle(text))
            FilterType.AUTHOR -> spec.and(filteredByAuthor(text))
            FilterType.CONTENT -> spec.and(filteredByContent(text))
            FilterType.REPLY_AUTHOR -> spec.and(filteredByReplyAuthor(text))
            FilterType.REPLY_CONTENT -> spec.and(filteredByReplyContent(text))
        }
    }

    enum class FilterType() {
        ALL,
        TITLE,
        CONTENT,
        AUTHOR,
        REPLY_AUTHOR,
        REPLY_CONTENT, ;

        companion object {
            fun paramToFilterType(param: String): FilterType {
                try {
                    return FilterType.valueOf(param.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw FilterTypeNotFoundException(param)
                }
            }
        }
    }
}
