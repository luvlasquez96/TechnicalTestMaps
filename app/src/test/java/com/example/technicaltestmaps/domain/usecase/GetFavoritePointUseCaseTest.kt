package com.example.technicaltestmaps.domain.usecase

import app.cash.turbine.test
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetFavoritePointUseCaseTest {
    @MockK
    private lateinit var repository: FavoritePointRepository

    @InjectMockKs
    private lateinit var useCase: GetFavoritePointUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetFavoritePointUseCase(repository)
    }

    @Test
    fun `GIVEN favorite points in repository WHEN use case is invoked THEN it emits the same list`(){
        runTest {
            val expectedPoints = listOf(
                FavoritePoint(1, "Point A", 10.0, 20.0, PointType.NORMAL),
                FavoritePoint(2, "Point B", 30.0, 40.0, PointType.NORMAL)
            )

            coEvery { repository.getAllFavoritePoints() } returns flowOf(expectedPoints)

            useCase().test {
                val actualPoints = awaitItem()
                assertEquals(expectedPoints, actualPoints)
                awaitComplete()
            }
        }
    }
}