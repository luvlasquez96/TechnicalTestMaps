package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteFavoritePointUseCaseTest {
    @MockK
    private lateinit var repository: FavoritePointRepository

    @InjectMockKs
    private lateinit var useCase: DeleteFavoritePointUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = DeleteFavoritePointUseCase(repository)
    }

    @Test
    fun `GIVEN an id WHEN invoke is called THEN repository deleteFavoritePoint is called`() {
        runTest {
            coEvery {
                repository.deleteFavoritePoint(any())

            } returns Unit

            val result = useCase.invoke(1)

            assertEquals(Unit, result)
            coVerify { repository.deleteFavoritePoint(1) }
        }
    }
}