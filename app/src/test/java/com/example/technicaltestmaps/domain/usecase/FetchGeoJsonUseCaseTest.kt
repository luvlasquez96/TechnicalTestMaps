package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import com.mapbox.geojson.FeatureCollection
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

class FetchGeoJsonUseCaseTest {
    @MockK
    private lateinit var repository: GeoJsonRepository

    @InjectMockKs
    private lateinit var useCase: FetchGeoJsonUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = FetchGeoJsonUseCase(repository)
    }

    @Test
    fun `GIVEN repository returns FeatureCollection WHEN use case is invoked THEN return same FeatureCollection`() {
        runTest {

            val expectedFeatureCollection = mockk<FeatureCollection>()
            coEvery { repository.fetchGeoJson() } returns expectedFeatureCollection

            val result = useCase()

            assertEquals(expectedFeatureCollection, result)
            coVerify(exactly = 1) { repository.fetchGeoJson() }
        }
    }

    @Test
    fun `GIVEN repository returns null WHEN use case is invoked THEN return null`() {
        runTest {
            coEvery { repository.fetchGeoJson() } returns null

            val result = useCase()

            assertNull(result)
            coVerify(exactly = 1) { repository.fetchGeoJson() }
        }
    }
}