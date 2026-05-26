package dev.oruizp.expensetracker.backend.graphql.scalars

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val DateScalarType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Date")
    .description("A date in ISO-8601 format (yyyy-MM-dd)")
    .coercing(object : Coercing<LocalDate, String> {
        override fun serialize(input: Any): String {
            return when (input) {
                is LocalDate -> input.format(DateTimeFormatter.ISO_LOCAL_DATE)
                is String -> input
                else -> throw CoercingSerializeException("Expected a LocalDate or String")
            }
        }

        override fun parseValue(input: Any): LocalDate {
            return when (input) {
                is String -> LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE)
                else -> throw CoercingParseLiteralException("Expected a String")
            }
        }

        override fun parseLiteral(input: Any): LocalDate {
            return when (input) {
                is StringValue -> LocalDate.parse(input.value, DateTimeFormatter.ISO_LOCAL_DATE)
                else -> throw CoercingParseLiteralException("Expected a StringValue")
            }
        }
    })
    .build()
