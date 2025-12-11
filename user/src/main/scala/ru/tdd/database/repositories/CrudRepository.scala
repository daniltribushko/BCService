package ru.tdd.database.repositories

import ru.tdd.database.entities.Dao
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.Future

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @tparam E класс модели Dao
 * @tparam T класс описывающий таблицу в Slick
 */
abstract class CrudRepository[E <: Dao, T <: EntityTable[E]](db: Database) {

  protected val entities: TableQuery[T]

  /** Сохранение объекта */
  final def save(entity: E): Future[Int] =
    db.run(entities += entity)

  /** Обновление объекта */
  final def update(newEntity: E): Future[Int] =
    db.run(entities.filter(_.id === newEntity.id).update(newEntity))

  /** Поиск объекта с условиями */
  final def findOne(condition: T => Rep[Boolean]): Future[Option[E]] =
    db.run(entities.filter(condition).result.headOption)

  /** Поиск нескольких объектов с условиями */
  final def findAll(condition: T => Rep[Boolean]): Future[Seq[E]] =
    db.run(entities.filter(condition).result)

  /** Поиск объектов по идентификатору */
  final def findById(id: UUID): Future[Option[E]] =
    db.run(entities.filter(_.id === id).result.headOption)

  /** Удаление по идентификатору */
  final def deleteById(id: UUID): Future[Int] =
    db.run(entities.filter(_.id === id).delete)

  /** Получение количества объектов по условию */
  final def count(condition: T => Rep[Boolean]): Future[Int] = db.run(entities.filter(condition).length.result)

  /** Проверка на существование объектов по условию */
  final def exists(condition: T => Rep[Boolean]): Future[Boolean] = db.run(entities.filter(condition).exists.result)
}
