package com.example.traveladvisor;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.traveladvisor.adapter.MapRecyclerViewAdapter;
import com.example.traveladvisor.bll.Location;
import com.example.traveladvisor.dal.DatabaseManager;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class MapFragment extends Fragment implements OnMapReadyCallback, PermissionsListener,
        MapboxMap.OnMapClickListener, Callback<DirectionsResponse> {
    private static final String TAG = "MapFragment";

    private static final String SOURCE_ID = "mapbox.poi";
    private static final String MAKI_LAYER_ID = "mapbox.poi.maki";
    private static final String LOADING_LAYER_ID = "mapbox.poi.loading";

    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_LOADING = "loading";
    private static final String PROPERTY_LOADING_PROGRESS = "loading_progress";
    private static final String PROPERTY_TITLE = "title";

    private static final long CAMERA_ANIMATION_TIME = 1950;
    private static final float LOADING_CIRCLE_RADIUS = 60;
    private static final int LOADING_PROGRESS_STEPS = 25;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private RecyclerView recyclerView;
    private Button buttonScanQr;
    private Button buttonLocateMe;
    private ProgressBar routeLoading;

    private NavigationMapRoute navigationMapRoute;

    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    private AnimatorSet animatorSet;

    private PermissionsManager permissionsManager;

    private ArrayList<Location> data = new ArrayList<Location>();
    private DirectionsRoute currentRoute;

    @ActivityStep
    private int currentStep;

    public MapFragment() {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STEP_INITIAL, STEP_LOADING, STEP_READY})
    public @interface ActivityStep {
    }

    private static final int STEP_INITIAL = 0;
    private static final int STEP_LOADING = 1;
    private static final int STEP_READY = 2;

    private static final Map<Integer, Double> stepZoomMap = new HashMap<>();

    static {
        stepZoomMap.put(STEP_INITIAL, 11.0);
        stepZoomMap.put(STEP_LOADING, 13.5);
        stepZoomMap.put(STEP_READY, 18.0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.access_token));

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        recyclerView = rootView.findViewById(R.id.rv_on_top_of_map);
        routeLoading = rootView.findViewById(R.id.routeLoadingProgressBar);
        buttonScanQr = rootView.findViewById(R.id.button_scan_qr);
        buttonLocateMe = rootView.findViewById(R.id.button_locate_me);

        buttonScanQr.setOnClickListener((View v) -> {
            Toast.makeText(getContext(), "Scan Qr Code", Toast.LENGTH_LONG).show();

            Intent myIntent = new Intent(this.getActivity(), QrCodeScan.class);
            this.startActivity(myIntent);
        });

        buttonLocateMe.setOnClickListener((View v) -> {
            android.location.Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            animateCameraToSelection(Feature.fromGeometry(Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude())), 14);
        });

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    public void setData(ArrayList<Location> data) {
        this.data = data;
    }

    public ArrayList<Location> getData() {
        return this.data;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mkleinegger/ck4movtpa21z71ctalv5mt7e5")
                , new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                        navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        mapboxMap.getUiSettings().setCompassEnabled(false);
                        mapboxMap.getUiSettings().setLogoEnabled(false);
                        mapboxMap.getUiSettings().setAttributionEnabled(false);
                        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
                        mapboxMap.setMinZoomPreference(5);
                        mapboxMap.addOnMapClickListener(MapFragment.this);

                        new AddLocationsToMapTask(MapFragment.this).execute();
                        Log.i(TAG, "Map loaded");
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), "This app needs location permissions in order to show its functionality.", Toast.LENGTH_LONG).show();
        Log.i(TAG, "This app needs location permissions in order to show its functionality.");
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getContext(), "You didn't grant location permissions.", Toast.LENGTH_LONG).show();
            Log.i(TAG, "You didn't grant location permissions.");
            getActivity().finish();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        deselectAll(true, true);
        refreshSource();

        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    public void setupData(final FeatureCollection collection) {
        if (mapboxMap == null) {
            return;
        }

        featureCollection = collection;
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                setupSource(style);
                setupMakiLayer(style);
                setupLoadingLayer(style);
                setupRecyclerView();
            }
        });
    }

    private void setupSource(@NonNull Style loadedMapStyle) {
        source = new GeoJsonSource(SOURCE_ID, featureCollection);
        loadedMapStyle.addSource(source);
    }

    private void refreshSource() {
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Setup a layer with maki icons, eg. markers.
     */
    private void setupMakiLayer(@NonNull Style loadedMapStyle) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_location_24dp, null);
        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);

        mapboxMap.getStyle().addImage("my.image", mBitmap);

        loadedMapStyle.addLayer(new SymbolLayer(MAKI_LAYER_ID, SOURCE_ID)
                .withProperties(
                        iconImage("my.image"),
                        iconAllowOverlap(true),
                        iconSize(match(Expression.toString(get(PROPERTY_SELECTED)), literal(1.0f),
                                stop("true", 1.5f))))
        );
    }

    /**
     * Setup layer indicating that there is an ongoing progress.
     */
    private void setupLoadingLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayerBelow(new CircleLayer(LOADING_LAYER_ID, SOURCE_ID)
                .withProperties(
                        circleRadius(interpolate(exponential(1), get(PROPERTY_LOADING_PROGRESS), getLoadingAnimationStops())),
                        circleColor(Color.GRAY),
                        circleOpacity(0.6f)
                )
                .withFilter(eq(get(PROPERTY_LOADING), literal(true))), MAKI_LAYER_ID);
    }

    private Expression.Stop[] getLoadingAnimationStops() {
        List<Expression.Stop> stops = new ArrayList<>();
        for (int i = 0; i < LOADING_PROGRESS_STEPS; i++) {
            stops.add(stop(i, LOADING_CIRCLE_RADIUS * i / LOADING_PROGRESS_STEPS));
        }

        return stops.toArray(new Expression.Stop[LOADING_PROGRESS_STEPS]);
    }

    private void setupRecyclerView() {
        RecyclerView.Adapter adapter = new MapRecyclerViewAdapter(this, this.data);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE) {
                    int index = layoutManager.findFirstVisibleItemPosition();
                    setSelected(index, false);

                }
            }
        });
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * This method handles click events for maki symbols.
     * <p>
     * When a maki symbol is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MAKI_LAYER_ID);
        if (!features.isEmpty()) {
            String title = features.get(0).getStringProperty(PROPERTY_TITLE);
            List<Feature> featureList = featureCollection.features();
            for (int i = 0; i < featureList.size(); i++) {
                if (featureList.get(i).getStringProperty(PROPERTY_TITLE).equals(title)) {
                    setSelected(i, true);
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Set a feature selected state with the ability to scroll the RecycleViewer to the provided index.
     *
     * @param index      the index of selected feature
     * @param withScroll indicates if the recyclerView position should be updated
     */
    private void setSelected(int index, boolean withScroll) {
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        deselectAll(false, false);

        Feature feature = featureCollection.features().get(index);
        selectFeature(feature);
        selectRoute(feature);
        animateCameraToSelection(feature);
        refreshSource();

        if (withScroll) {
            recyclerView.scrollToPosition(index);
        }
    }

    /**
     * Deselects the state of all the features
     */
    private void deselectAll(boolean hideRecycler, boolean removeRoute) {
        if (featureCollection.features().size() > 0) {
            for (Feature feature : featureCollection.features()) {
                feature.properties().addProperty(PROPERTY_SELECTED, false);
            }
        }

        if (hideRecycler) {
            recyclerView.setVisibility(View.GONE);
        }

        if (removeRoute) {
            removeRoute();
        }
    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void selectFeature(Feature feature) {
        feature.properties().addProperty(PROPERTY_SELECTED, true);
    }

    private Feature getSelectedFeature() {
        if (featureCollection != null) {
            for (Feature feature : featureCollection.features()) {
                if (feature.getBooleanProperty(PROPERTY_SELECTED)) {
                    return feature;
                }
            }
        }

        return null;
    }

    /**
     * Entry point to start the navigation
     */

    public void startNavigation() {
        boolean simulateRoute = false;

        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();

        NavigationLauncher.startNavigation(getActivity(), options);
    }

    /**
     * Selectes the perfect Route for Navigation
     */

    private void selectRoute(Feature feature) {
        android.location.Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
        findRoute(Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()), (Point) feature.geometry());

        routeLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Navigation SDK
     */
    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (response.isSuccessful() && response.body() != null && !response.body().routes().isEmpty()) {
            //for more than one route
            //List<DirectionsRoute> routes = response.body().routes();
            //navigationMapRoute.addRoutes(routes);

            //for the fastest route
            currentRoute = response.body().routes().get(0);
            navigationMapRoute.addRoute(currentRoute);

            routeLoading.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        Log.e(TAG, t.getMessage(), t);
    }

    public void findRoute(Point origin, Point destination) {
        NavigationRoute.builder(getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(this);
    }

    private void removeRoute() {
        navigationMapRoute.updateRouteVisibilityTo(false);
    }

    /**
     * Animate camera to a feature.
     *
     * @param feature the feature to animate to
     */
    private void animateCameraToSelection(Feature feature, double newZoom) {
        CameraPosition cameraPosition = mapboxMap.getCameraPosition();

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createLatLngAnimator(cameraPosition.target, convertToLatLng(feature)),
                createZoomAnimator(cameraPosition.zoom, newZoom)
        );
        animatorSet.start();
    }

    private void animateCameraToSelection(Feature feature) {
        double zoom = feature.getNumberProperty("zoom").doubleValue();
        animateCameraToSelection(feature, zoom);
    }

    private void setActivityStep(@ActivityStep int activityStep) {
        Feature selectedFeature = getSelectedFeature();
        double zoom = stepZoomMap.get(activityStep);
        animateCameraToSelection(selectedFeature, zoom);

        currentStep = activityStep;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        if (navigationMapRoute != null) {
            navigationMapRoute.onStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (navigationMapRoute != null) {
            navigationMapRoute.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        if (currentStep == STEP_LOADING || currentStep == STEP_READY) {
            setActivityStep(STEP_INITIAL);
            deselectAll(true, true);
            refreshSource();
        } else {
            super.onDetach();
        }
    }

    private LatLng convertToLatLng(Feature feature) {
        Point symbolPoint = (Point) feature.geometry();
        return new LatLng(symbolPoint.latitude(), symbolPoint.longitude());
    }

    private Animator createLatLngAnimator(LatLng currentPosition, LatLng targetPosition) {
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new LatLngEvaluator(), currentPosition, targetPosition);
        latLngAnimator.setDuration(CAMERA_ANIMATION_TIME);
        latLngAnimator.setInterpolator(new FastOutSlowInInterpolator());
        latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng) animation.getAnimatedValue()));
            }
        });
        return latLngAnimator;
    }

    private Animator createZoomAnimator(double currentZoom, double targetZoom) {
        ValueAnimator zoomAnimator = ValueAnimator.ofFloat((float) currentZoom, (float) targetZoom);
        zoomAnimator.setDuration(CAMERA_ANIMATION_TIME);
        zoomAnimator.setInterpolator(new FastOutSlowInInterpolator());
        zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapboxMap.moveCamera(CameraUpdateFactory.zoomTo((Float) animation.getAnimatedValue()));
            }
        });
        return zoomAnimator;
    }

    /**
     * Helper class to evaluate LatLng objects with a ValueAnimator
     */
    private static class LatLngEvaluator implements TypeEvaluator<LatLng> {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    }

    /**
     * AsyncTask to add data to the Map
     */
    private static class AddLocationsToMapTask extends AsyncTask<Void, Void, FeatureCollection> {

        private final WeakReference<MapFragment> activityRef;

        AddLocationsToMapTask(MapFragment activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            try {
                activityRef.get().setData(DatabaseManager.newInstance().getAllLocations());
            } catch (Exception e) {
                Toast.makeText(activityRef.get().getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, e.getMessage(), e);
            }
        }


        @Override
        protected FeatureCollection doInBackground(Void... params) {
            MapFragment activity = activityRef.get();

            if (activity == null) {
                return null;
            }

            List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

            for (Location location : activity.getData()) {
                Feature feature = Feature.fromGeometry(Point.fromLngLat(location.getKoordinaten().getLon(), location.getKoordinaten().getLat()));
                feature.addStringProperty("title", location.getBezeichnung());
                feature.addStringProperty("description", location.getBeschreibung());
                feature.addBooleanProperty("selected", false);
                feature.addBooleanProperty("loading", false);
                feature.addBooleanProperty("favourite", false);
                feature.addNumberProperty("zoom", 14);

                symbolLayerIconFeatureList.add(feature);
            }

            return FeatureCollection.fromFeatures(symbolLayerIconFeatureList);
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection) {
            super.onPostExecute(featureCollection);
            MapFragment activity = activityRef.get();
            if (featureCollection == null || activity == null) {
                return;
            }

            activity.setupData(featureCollection);
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}