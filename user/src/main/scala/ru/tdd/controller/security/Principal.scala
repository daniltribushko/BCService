package ru.tdd.controller.security

import ru.tdd.app.models.enums.Role

import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 */
case class Principal(id: UUID, username: String, roles: Seq[Role])
