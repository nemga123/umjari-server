package com.umjari.server.global.model

import jakarta.persistence.EntityManager

open class CUDRepository<T> (
    private val entityManager: EntityManager,
) {
    fun insert(entity: T) {
        entityManager.persist(entity)
    }

    fun insertAll(entities: Collection<T>) {
        for (entity: T in entities) {
            insert(entity)
        }
    }

    fun update(entity: T) {
        entityManager.merge(entity)
    }

    fun delete(entity: T) {
        entityManager.remove(if (entityManager.contains(entity)) entity else entityManager.merge(entity))
    }

    fun deleteAll(entities: Collection<T>) {
        for (entity: T in entities) {
            delete(entity)
        }
    }
}
