package com.example.technicaltestmaps.presentation

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.domain.usecase.AddFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.DeleteFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.FetchGeoJsonUseCase
import com.example.technicaltestmaps.domain.usecase.GetFavoritePointUseCase
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapViewModelTest {

    @MockK
    private lateinit var addFavoritePointUseCase: AddFavoritePointUseCase

    @MockK
    private lateinit var deleteFavoritePointUseCase: DeleteFavoritePointUseCase

    @MockK
    private lateinit var getFavoritePointUseCase: GetFavoritePointUseCase

    @MockK
    private lateinit var fetchGeoJsonUseCase: FetchGeoJsonUseCase

    private lateinit var viewModel: MapViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        Dispatchers.setMain(testDispatcher)

        every { getFavoritePointUseCase() } returns flowOf(emptyList())

        viewModel = MapViewModel(
            addFavoritePointUseCase,
            deleteFavoritePointUseCase,
            getFavoritePointUseCase,
            fetchGeoJsonUseCase
        )
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadFavoritePoints should update favoritePoints state`() = runTest {
        val expectedPoints = listOf(
            FavoritePoint(1, "Park", 1.0, 1.0, PointType.NORMAL)
        )
        coEvery { getFavoritePointUseCase() } returns flowOf(expectedPoints)

        viewModel = MapViewModel(
            addFavoritePointUseCase,
            deleteFavoritePointUseCase,
            getFavoritePointUseCase,
            fetchGeoJsonUseCase
        )

        advanceUntilIdle()

        assertEquals(expectedPoints, viewModel.favoritePoints.value)
    }

    @Test
    fun `onMapLongClick should update pendingPointToSave`() {
        runTest {
            val point = Point.fromLngLat(-75.0, 6.0)

            viewModel.onMapLongClick(point)

            assertEquals(point, viewModel.pendingPointToSave.value)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveFavoritePoint should call use case and clear pendingPointToSave`() {
        runTest {
            val mapPoint = Point.fromLngLat(-75.0, 6.0)
            viewModel.onMapLongClick(mapPoint)

            coEvery { addFavoritePointUseCase(any()) } returns Unit

            viewModel.saveFavoritePoint("Vet", PointType.NORMAL)

            advanceUntilIdle()

            coVerify(exactly = 1) {
                addFavoritePointUseCase(withArg {
                    assertEquals("Vet", it.name)
                    assertEquals(6.0, it.latitude, 0.001)
                    assertEquals(-75.0, it.longitude, 0.001)
                    assertEquals(PointType.NORMAL, it.type)
                })
            }
            assertNull(viewModel.pendingPointToSave.value)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deletePoint should call use case and clear selectedFavoritePoint if IDs match`() {
        runTest {
            val point = FavoritePoint(1, "Place", 0.0, 0.0, PointType.NORMAL)
            viewModel.selectFavoritePoint(point)

            coEvery { deleteFavoritePointUseCase(1) } returns Unit

            viewModel.deletePoint(1)

            advanceUntilIdle()

            coVerify(exactly = 1) { deleteFavoritePointUseCase(1) }
            assertNull(viewModel.selectedFavoritePoint.value)
        }
    }

    @Test
    fun `updateUserLocation should update state`() = runTest {
        val location = Point.fromLngLat(-74.0, 4.0)
        viewModel.updateUserLocation(location)

        assertEquals(location, viewModel.userLocation.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadGeoJson should update featureCollection state`() {
        runTest {

            val mockFeatureCollection = mockk<FeatureCollection>()
            coEvery { fetchGeoJsonUseCase() } returns mockFeatureCollection

            viewModel = MapViewModel(
                addFavoritePointUseCase,
                deleteFavoritePointUseCase,
                getFavoritePointUseCase,
                fetchGeoJsonUseCase
            )

            advanceUntilIdle()

            assertEquals(mockFeatureCollection, viewModel.featureCollection.value)
        }
    }
}