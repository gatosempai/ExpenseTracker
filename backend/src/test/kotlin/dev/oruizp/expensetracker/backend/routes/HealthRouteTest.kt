package dev.oruizp.expensetracker.backend.routes

import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertTrue

class HealthRouteTest {

    @Test
    fun `health endpoint returns UP status`() {
        withTestApplication({
            registerHealthRoutes()
        }) {
            val response = handleRequest {
                uri = "/health"
                method = io.ktor.http.HttpMethod.Get
            }
            val content = response.response.content ?: ""
            org.junit.Assert.assertTrue("Response should contain 'UP', got: $content", content.contains("UP"))
            org.junit.Assert.assertTrue("Response should contain 'status'", content.contains("status"))
        }
    }
}
