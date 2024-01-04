package tn.esprit.ecocircuitapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.offline.OfflineResponseInfo
import com.mapbox.search.offline.OfflineSearchEngine
import com.mapbox.search.offline.OfflineSearchEngineSettings
import com.mapbox.search.offline.OfflineSearchResult
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.ui.adapter.engines.SearchEngineUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import tn.esprit.ecocircuitapp.R
import tn.esprit.ecocircuitapp.databinding.ActivityMapBinding
import tn.esprit.ecocircuitapp.models.DonationPlace
import tn.esprit.ecocircuitapp.utilities.LocationPermissionHelper
import tn.esprit.ecocircuitapp.viewModels.DonationPlaceViewModel
import java.lang.ref.WeakReference
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MapActivity : AppCompatActivity() {
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }


    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }


        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }


        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMapBinding




    private var  isMapReady: Boolean = false

    private lateinit var searchEngine: SearchEngine


    private var mapStyleLoaded = false
    private var currentMapStyle: Style? = null

    private lateinit var orgViewModel: DonationPlaceViewModel

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView


        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))


        orgViewModel = ViewModelProvider(this).get(DonationPlaceViewModel::class.java)

        orgViewModel.getAllDonationPlaces().observe(this, { orgList ->
            if (orgList != null) {
                addorgAnnotations(orgList)
            } else {
                Log.e(TAG, "Error getting ZoneDeDanger")
            }
        })




        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings(getString(R.string.mapbox_access_token))
        )
        val queryEditText = findViewById<EditText>(R.id.query_edit_text)
        val searchResultsView = findViewById<SearchResultsView>(R.id.search_results_view)
        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )


        val offlineSearchEngine = OfflineSearchEngine.create(
            OfflineSearchEngineSettings(getString(R.string.mapbox_access_token))
        )


        val    searchEngineUiAdapter = SearchEngineUiAdapter(
            view = searchResultsView,
            searchEngine = searchEngine,
            offlineSearchEngine = offlineSearchEngine,
        )


        searchEngineUiAdapter.addSearchListener(object : SearchEngineUiAdapter.SearchListener {


            private fun showToast(message: String) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }


            override fun onSuggestionsShown(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                // not implemented
            }


            override fun onSearchResultsShown(
                suggestion: SearchSuggestion,
                results: List<SearchResult>,
                responseInfo: ResponseInfo
            ) {
                // not implemented
            }


            override fun onOfflineSearchResultsShown(results: List<OfflineSearchResult>, responseInfo: OfflineResponseInfo) {
                // not implemented
            }


            override fun onSuggestionSelected(searchSuggestion: SearchSuggestion): Boolean {
                return false
            }


            override fun onSearchResultSelected(searchResult: SearchResult, responseInfo: ResponseInfo) {
                // Assuming you have a reference to your MapboxMap object as 'mapboxMap'
                val newCameraPosition = CameraOptions.Builder()
                    .center(searchResult.coordinate)
                    .zoom(14.0) // or any other zoom level you prefer
                    .build()
                mapView.getMapboxMap().setCamera(newCameraPosition)
            }


            override fun onOfflineSearchResultSelected(searchResult: OfflineSearchResult, responseInfo: OfflineResponseInfo) {
                val newCameraPosition = CameraOptions.Builder()
                    .center(searchResult.coordinate)
                    .zoom(14.0) // or any other zoom level you prefer
                    .build()
                mapView.getMapboxMap().setCamera(newCameraPosition)
            }


            override fun onError(e: Exception) {
                showToast("Error happened: $e")
            }


            override fun onHistoryItemClick(historyRecord: HistoryRecord) {
                val newCameraPosition = CameraOptions.Builder()
                    .center(historyRecord.coordinate)
                    .zoom(14.0) // or any other zoom level you prefer
                    .build()
                mapView.getMapboxMap().setCamera(newCameraPosition)
            }


            override fun onPopulateQueryClick(suggestion: SearchSuggestion, responseInfo: ResponseInfo) {
                queryEditText.setText(suggestion.name)
            }


            override fun onFeedbackItemClick(responseInfo: ResponseInfo) {
                // not implemented
            }
        })
        queryEditText.setOnClickListener {
            // Trigger the search when the EditText is clicked
            searchEngineUiAdapter.search(queryEditText.text.toString())
        }
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                if (s.length >= 3) {
                    searchEngineUiAdapter.search(s.toString())
                } else {
                    // No need to manually set the adapter to null, let the SearchEngineUiAdapter handle it
                }
            }


            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // not implemented
            }


            override fun afterTextChanged(e: Editable) {
                // not implemented
            }
        })


        // Set up map tap listener to hide the SearchResultsView
        mapView.getMapboxMap().addOnMapClickListener { point ->
            toggleSearchBarVisibility(false)
            false // return false to indicate the click event is not consumed
        }

        // Set up edit text focus listener to toggle the visibility of SearchResultsView
        queryEditText.setOnFocusChangeListener { _, hasFocus ->
            toggleSearchBarVisibility(hasFocus)
        }

    }

    private fun addAnnotationToMap() {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@MapActivity,
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(45.1, -74.0))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    private companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0


        fun Context.isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun onMapReady() {
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {style ->
            currentMapStyle = style
            mapStyleLoaded = true

           isMapReady = true
            loadorganazationData()




            addAnnotationToMap()
            initLocationComponent()
            setupGesturesListener()


            val latitude = userLatitude
            val longitude = userLongitude


            if (latitude != null && longitude != null) {
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(longitude, latitude))
                        .zoom(1.0)
                        .build()
                )
            }
        }
    }


    private fun toggleSearchBarVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        findViewById<SearchResultsView>(R.id.search_results_view).visibility = visibility
    }








    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }


    private var userLatitude: Double? = null
    private var userLongitude: Double? = null


    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MapActivity,
                    R.drawable.baseline_location_on_24
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MapActivity,
                    R.drawable.baseline_location_on_24
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }


        locationComponentPlugin.addOnIndicatorPositionChangedListener { point ->
            userLatitude = point.latitude()
            userLongitude = point.longitude()
            //Log.d("User Location", "Latitude: $userLatitude, Longitude: $userLongitude")
        }


        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }
    private fun loadorganazationData() {
        if (isMapReady) {
            orgViewModel.getAllDonationPlaces().observe(this, { orgList ->
                if (orgList != null) {
                    // Log the count of ZoneDeDanger items received
                    Log.d(TAG, "Received ${orgList.size} ZoneDeDanger items from backend")

                    addorgAnnotations(orgList)
                } else {
                    Log.e(TAG, "Error getting ZoneDeDanger")
                }
            })
        } else {
            Log.e(TAG, "Map is not ready")
        }
    }


    private fun addorgAnnotations(orgList: List<DonationPlace>) {
        val annotationApi = mapView?.annotations
        val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)

        orgList.forEach { org ->
            bitmapFromDrawableRes(this@MapActivity, R.drawable.baseline_location_on_24)?.let { bitmap ->
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(org.latitude.toDouble(), org.longitude.toDouble()))
                    .withIconImage(bitmap)
                    .withIconSize(1.0) // Use Float value here

                pointAnnotationManager?.create(pointAnnotationOptions)
                Log.e(TAG, "ZoneDeDanger added to map")
            }
        }
    }



    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }


    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


}

