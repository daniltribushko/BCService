package ru.tdd.telegram_bot.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.tdd.telegram_bot.controller.jackson.BotCommandDeserializer;
import ru.tdd.telegram_bot.controller.jackson.BotCommandSerializer;
import ru.tdd.telegram_bot.model.enums.BotCommand;

import java.io.Serializable;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto команды бота
 */
public class BotCommandDTO implements Serializable {

    @JsonSerialize(using = BotCommandSerializer.class)
    @JsonDeserialize(using = BotCommandDeserializer.class)
    private BotCommand command;

    private String body;

    public BotCommandDTO() {

    }

    public BotCommandDTO(BotCommand command, String body) {
        this.command = command;
        this.body = body;
    }

    public BotCommand getCommand() {
        assert command != null;
        return command;
    }

    public void setCommand(BotCommand command) {
        this.command = command;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
