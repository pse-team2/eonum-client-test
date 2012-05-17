package ch.eonum.test;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import ch.eonum.HealthActivity;
import android.app.Instrumentation;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HealthActivityTest extends ActivityInstrumentationTestCase2<HealthActivity>
{
	private HealthActivity mActivity; // The activity under test
	private TextView whatView, whereView; // The activity's AutoCompleteTextView
	private String whatString, whereString;
	private AutoCompleteTextView whatText, whereText;
	private MenuItem mAbout;
	private MapView mapView;
	private Button searchButton;
	private ImageButton positionButton;

	/**
	 * This constructor has no parameters, and its sole purpose is to pass information to the superclass's
	 * default constructor.
	 */
	public HealthActivityTest()
	{
		super("ch.eonum", HealthActivity.class);
	}
	// End of HealthActivityTest constructor definition

	@Override
	protected void setUp() throws Exception
	{
		/* Invokes the superclass constructor for setUp(), which is required by JUnit. */
		super.setUp();

		/* This turns off touch mode in the device or emulator. If any of your test methods send key events to
		 * the application, you must turn off touch mode before you start any activities; otherwise, the call
		 * is ignored. */
		setActivityInitialTouchMode(false);

		// Gets a reference to the activity under test (HealthActivity).
		// This call also starts the activity if it is not already running.
		this.mActivity = this.getActivity();

		// Gets a reference to the MapView
		this.mapView = (MapView) this.mActivity.findViewById(ch.eonum.R.id.mapview);

		this.whatView = (TextView) this.mActivity.findViewById(ch.eonum.R.id.textviewWhat);
		this.whatText = (AutoCompleteTextView) this.mActivity.findViewById(ch.eonum.R.id.searchforWhat);
		this.whereView = (TextView) this.mActivity.findViewById(ch.eonum.R.id.textviewWhere);
		this.whereText = (AutoCompleteTextView) this.mActivity.findViewById(ch.eonum.R.id.searchforWhere);
		this.whatString = this.mActivity.getString(ch.eonum.R.string.what);
		this.whereString = this.mActivity.getString(ch.eonum.R.string.where);

		this.positionButton = (ImageButton) this.mActivity.findViewById(ch.eonum.R.id.getposition);
		this.searchButton = (Button) this.mActivity.findViewById(ch.eonum.R.id.search);

		// Gets a reference to the menu of the application under test.
		this.mAbout = (MenuItem) this.mActivity.findViewById(ch.eonum.R.id.menu_mAbout);
	}
	// End of setUp() method definition

	/**
	 * The initial conditions test verifies that the application under test is initialized correctly.
	 * It is an illustration of the types of tests you can run, so it is not comprehensive.
	 */
	public void testPreconditions()
	{
		assertNotNull(this.mActivity);
		assertNotNull(this.whatText);
		assertNotNull(this.whereText);
		assertNotNull(this.whatView);
		assertNotNull(this.whereView);
		assertNotNull(this.mAbout);
	}
	// End of testPreConditions() method definition

	public void testText()
	{
		assertEquals(this.whatString, (String) this.whatView.getText());
		assertEquals(this.whereString, (String) this.whereView.getText());
	}

	@UiThreadTest
	public void testAutoCompleteTextView()
	{
		/* The @UiThreadTest annotation tells Android to build this method so that it runs on the UI thread.
		 * This allows the method to change the state of the AutoCompleteTextView in the application under test.
		 * This use of @UiThreadTest shows that, if necessary, you can run an entire method on the UI thread. */

		assertTrue(this.whatText.performClick());

		/* Send key events to the AutoCompleteTextView searchForWhat to select one of the items.
		 * To do this, open the AutoCompleteTextView by "pressing" the "A" button
		 * (sending a KeyCode event) and then clicking (sending) the down arrow keypad button four times.
		 * Finally, click the center keypad button to highlight the desired item. */
		this.sendKeys(KeyEvent.KEYCODE_A);
		for (int i = 1; i <= 4; i++)
		{
			// This sets the final position of the AutoCompleteTextView searchForWhat to "Allgemeinärzte"
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		} // End of for loop

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		/* Query the current state of the AutoCompleteTextView searchForWhat
		 * and compare its current selection to the expected value.
		 * Call the method getText() to find out the text that is being displayed in the text field. */
		String selection = this.whatText.getText().toString();
		String stringResource = this.mActivity.getString(ch.eonum.R.string.allgemeinaerzte);

		// Assert that this string value matches the expected value of "Allgemeinärzte".
		assertEquals(stringResource, selection);
	}

	public void testLocationService()
	{
		double lat = 46.9513449;
		double lng = 7.4384537;
		LocationManager testLocMgr = (LocationManager) this.mActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);

		// Set up test

		Location location = new Location("TestProvider");
		location.setLatitude(lat);
		location.setLongitude(lng);
		testLocMgr.setTestProviderLocation("TestProvider", location);

		// Check if your listener reacted the right way
		double currentLatitude = mActivity.getLocation().getLatitude();
		double currentLongitude = mActivity.getLocation().getLongitude();

		assertEquals(lat, currentLatitude);
		assertEquals(lng, currentLongitude);

		testLocMgr.removeTestProvider("TestProvider");
	}

	public void testMyLocation()
	{
		double lat1 = 46.9513449;
		double lng1 = 7.4384537;
		double lat2 = 46.94809;
		double lng2 = 7.44744;

		LocationManager testLocMgr = (LocationManager) this.mActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);

		// Trigger location update
		Location location = new Location("TestProvider");
		location.setLatitude(lat1);
		location.setLongitude(lng1);
		testLocMgr.setTestProviderLocation("TestProvider", location);

		GeoPoint initialCenterPoint = this.mapView.getMapCenter();

		testLocMgr.removeTestProvider("TestProvider");

		// Verify if the MapView has changed
		assertEquals((int)(lat1 * 1000000), this.mapView.getMapCenter().getLatitudeE6());
		assertEquals((int)(lng1 * 1000000), this.mapView.getMapCenter().getLongitudeE6());

		// Go to a different location
		GeoPoint differentPoint = new GeoPoint((int)(lat2 * 1000000), (int)(lng2 * 1000000));
		this.mapView.getController().setCenter(differentPoint);

		assertNotSame(initialCenterPoint.getLatitudeE6(), differentPoint.getLatitudeE6());
		assertNotSame(initialCenterPoint.getLongitudeE6(), differentPoint.getLongitudeE6());

		// Terminate the activity and restart it
		this.mActivity.finish();
		this.mActivity = this.getActivity();

		// Verify if the MapView has restored its state
		assertEquals(differentPoint.getLatitudeE6(), this.mapView.getMapCenter().getLatitudeE6());
		assertEquals(differentPoint.getLongitudeE6(), this.mapView.getMapCenter().getLongitudeE6());

		// Reset the map view to initialCenterPoint clicking the my location button
		this.positionButton.performClick();

		assertEquals(initialCenterPoint.getLatitudeE6(), this.mapView.getMapCenter().getLatitudeE6());
		assertEquals(initialCenterPoint.getLongitudeE6(), this.mapView.getMapCenter().getLongitudeE6());

		double currentLatitude = mActivity.getLocation().getLatitude();
		double currentLongitude = mActivity.getLocation().getLongitude();

		assertEquals(currentLatitude, initialCenterPoint.getLatitudeE6());
		assertEquals(currentLongitude, initialCenterPoint.getLongitudeE6());
	}

	public void testLocationManagerState()
	{
		LocationManager testLocMgr = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);
		LocationProvider TEST_PROVIDER = testLocMgr.getProvider("TestProvider");

		/* Set up instrumentation. Get the instrumentation object that is controlling the application under test.
		 * This is used later to invoke the onPause() and onResume() methods. */

		Instrumentation mInstr = this.getInstrumentation();

		// Use instrumentation to call the Activity's onPause()

		mInstr.callActivityOnPause(mActivity);

		/* Under test, the activity is waiting for input. The invocation of
		 * callActivityOnPause(android.app.Activity) performs a call directly to the activity's onPause()
		 * instead of manipulating the activity's UI to force it into a paused state. */

		// Test if location were disabled
		assertNull(testLocMgr);

		/* This ensures that resuming the activity actually restores the location manager rather than simply
		 * leaving it as it was. */

		// Use instrumentation to call the Activity's onResume():
		mInstr.callActivityOnResume(mActivity);

		/* Invoking callActivityOnResume(android.app.Activity) affects the activity in a way similar to
		 * callActivityOnPause. The activity's onResume() method is invoked instead of manipulating the
		 * activity's UI to force it to resume. */

		assertNotNull(testLocMgr);
		assertEquals(TEST_PROVIDER, testLocMgr.getProvider("TestProvider"));
	}

	public void testSearch()
	{
		this.whereText.setText("Tscharnerstrasse 41 3007 Bern");
		this.searchButton.performClick();
		//TODO: Test MapView
	}

}
