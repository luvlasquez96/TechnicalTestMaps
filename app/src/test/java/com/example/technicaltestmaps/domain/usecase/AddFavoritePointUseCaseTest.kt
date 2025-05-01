package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddFavoritePointUseCaseTest {

    @MockK
    private lateinit var repository: FavoritePointRepository

    @InjectMockKs
    private lateinit var useCase: AddFavoritePointUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = AddFavoritePointUseCase(repository)
    }

    @Test
    fun `GIVEN a point WHEN invoke is called THEN repository addFavoritePoint is called`() {
        runTest {
            coEvery {
                repository.addFavoritePoint(any())
            } returns Unit

            val point = mockk<FavoritePoint>()

            val result = useCase.invoke(point)

            assertEquals(Unit, result)
            coVerify { repository.addFavoritePoint(point) }
        }
    }
}