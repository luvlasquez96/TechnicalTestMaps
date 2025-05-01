package com.example.technicaltestmaps.data.local

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FavoritePointRepositoryImplTest {

    @MockK
    private lateinit var favoritePointDao: FavoritePointDao

    @InjectMockKs
    private lateinit var favoritePointRepository: FavoritePointRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        favoritePointRepository = FavoritePointRepositoryImpl(favoritePointDao)
    }

    @Test
    fun `GIVEN a point WHEN addFavoritePoint is called THEN dao insertPoint is called`() {
        runTest {

            val point = FavoritePoint(1, "Test", 1.0, 2.0, PointType.NORMAL)
            coEvery { favoritePointDao.insertPoint(any()) } returns Unit

            val result = favoritePointRepository.addFavoritePoint(point)

            assert(result == Unit)

            coVerify { favoritePointDao.insertPoint(point.toEntity()) }
        }
    }

    @Test
    fun `WHEN getAllFavoritePoints is called THEN a list of FavoritePoint is returned`() {
        runTest {
            val entities = listOf(
                FavoritePointEntity(1, "A", 1.0, 1.0, PointType.NORMAL.name),
                FavoritePointEntity(2, "B", 2.0, 2.0, PointType.ALERT.name)
            )
            val flow = flowOf(entities)
            coEvery { favoritePointDao.getAllPoints() } returns flow

            val result = favoritePointRepository.getAllFavoritePoints().first()

            assertEquals(entities.map { it.toDomain() }, result)
            coVerify { favoritePointDao.getAllPoints() }
            assertEquals(
                entities.map { it.toDomain().type },
                result.map { it.type }
            )
        }
    }

    @Test
    fun `GIVEN an id WHEN deleteFavoritePoint is called THEN dao deletePointById is called`() {
        runTest {
            val id = 1
            coEvery { favoritePointDao.deletePointById(id) } returns Unit

            val result = favoritePointRepository.deleteFavoritePoint(id)

            assert(result == Unit)
            coVerify { favoritePointDao.deletePointById(id) }
        }
    }

}