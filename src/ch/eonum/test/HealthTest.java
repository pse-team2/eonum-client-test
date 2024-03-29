package ch.eonum.test;

import ch.eonum.StartActivity;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.Smoke;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.jayway.android.robotium.solo.Solo;

public class HealthTest extends ActivityInstrumentationTestCase2<StartActivity>
{

	private Solo solo;

	public HealthTest()
	{
		super("ch.eonum", StartActivity.class);
	}

	@Override
	public void setUp()
	{
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown()
	{
		// Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}

	@Smoke
	public void testAboutActivity()
	{
		// Assert HealthActivity
		solo.assertCurrentActivity("Expected StartActivity activity", "StartActivity");
		solo.clickOnMenuItem("About", true);

		// Assert About Activity
		solo.assertCurrentActivity("Expected About activity", "About");

		// Assert that text is found
		assertTrue("Text 'Arztsuche' not found", solo.searchText("Arztsuche"));
		assertTrue("Text 'eonum' not found", solo.searchText("eonum"));
	}

	@UiThreadTest
	public void testAutoCompleteTextView()
	{
		/* The @UiThreadTest annotation tells Android to build this method so that it runs on the UI thread.
		 * This allows the method to change the state of the AutoCompleteTextView in the application under test.
		 * This use of @UiThreadTest shows that, if necessary, you can run an entire method on the UI thread. */

		Activity currentActivity = solo.getCurrentActivity();
		assertNotNull("Current activity is null", currentActivity);
		AutoCompleteTextView tv = (AutoCompleteTextView) solo.getView(ch.eonum.R.id.searchforWhat);
		assertNotNull("TextView is null", tv);
		solo.clickOnEditText(1);
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
		String selection = solo.getEditText(1).getText().toString();
		String stringResource = solo.getString(ch.eonum.R.string.allgemeinaerzte);

		// Assert that this string value matches the expected value of "Allgemeinärzte".
		assertEquals(stringResource, selection);
	}

	public void testLocationService()
	{
		double lat = 46.9513449;
		double lng = 7.4384537;
		Activity currentActivity = solo.getCurrentActivity();
		LocationManager testLocMgr = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);

		// Set up test
		Location location = new Location("TestProvider");
		location.setLatitude(lat);
		location.setLongitude(lng);
		testLocMgr.setTestProviderLocation("TestProvider", location);

		// Check if your listener reacted the right way
		double currentLatitude = ((ch.eonum.HealthActivity) currentActivity).getLocation().getLatitude();
		double currentLongitude = ((ch.eonum.HealthActivity) currentActivity).getLocation().getLongitude();

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

		Activity currentActivity = solo.getCurrentActivity();
		MapView map = (MapView) solo.getView(ch.eonum.R.id.mapview);
		LocationManager testLocMgr = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);

		// Trigger location update
		Location location = new Location("TestProvider");
		location.setLatitude(lat1);
		location.setLongitude(lng1);
		testLocMgr.setTestProviderLocation("TestProvider", location);

		GeoPoint initialCenterPoint = map.getMapCenter();

		testLocMgr.removeTestProvider("TestProvider");

		// Verify if the MapView has changed
		assertEquals((int)(lat1 * 1000000), map.getMapCenter().getLatitudeE6());
		assertEquals((int)(lng1 * 1000000), map.getMapCenter().getLongitudeE6());

		// Go to a different location
		GeoPoint differentPoint = new GeoPoint((int)(lat2 * 1000000), (int)(lng2 * 1000000));
		map.getController().setCenter(differentPoint);

		assertNotSame(initialCenterPoint.getLatitudeE6(), differentPoint.getLatitudeE6());
		assertNotSame(initialCenterPoint.getLongitudeE6(), differentPoint.getLongitudeE6());

		// Terminate the activity and restart it
		currentActivity.finish();
		currentActivity = this.getActivity();

		// Verify if the MapView has restored its state
		assertEquals(differentPoint.getLatitudeE6(), map.getMapCenter().getLatitudeE6());
		assertEquals(differentPoint.getLongitudeE6(), map.getMapCenter().getLongitudeE6());

		// Reset the map view to initialCenterPoint clicking the my location button
		solo.clickOnImageButton(0);

		assertEquals(initialCenterPoint.getLatitudeE6(), map.getMapCenter().getLatitudeE6());
		assertEquals(initialCenterPoint.getLongitudeE6(), map.getMapCenter().getLongitudeE6());

		double currentLatitude = ((ch.eonum.HealthActivity) currentActivity).getLocation().getLatitude();
		double currentLongitude = ((ch.eonum.HealthActivity) currentActivity).getLocation().getLongitude();

		assertEquals(currentLatitude, initialCenterPoint.getLatitudeE6());
		assertEquals(currentLongitude, initialCenterPoint.getLongitudeE6());
	}

	public void testLocationManagerState()
	{
		Activity currentActivity = solo.getCurrentActivity();
		LocationManager testLocMgr = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
		testLocMgr.addTestProvider("TestProvider", false, false, false, false, false, false, false,
			Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		testLocMgr.setTestProviderEnabled("TestProvider", true);
		LocationProvider TEST_PROVIDER = testLocMgr.getProvider("TestProvider");

		/* Set up instrumentation. Get the instrumentation object that is controlling the application under test.
		 * This is used later to invoke the onPause() and onResume() methods. */

		Instrumentation mInstr = this.getInstrumentation();

		// Use instrumentation to call the Activity's onPause()

		mInstr.callActivityOnPause(currentActivity);

		/* Under test, the activity is waiting for input. The invocation of
		 * callActivityOnPause(android.app.Activity) performs a call directly to the activity's onPause()
		 * instead of manipulating the activity's UI to force it into a paused state. */

		// Test if location were disabled
		assertNull(testLocMgr);

		/* This ensures that resuming the activity actually restores the location manager rather than simply
		 * leaving it as it was. */

		// Use instrumentation to call the Activity's onResume():
		mInstr.callActivityOnResume(currentActivity);

		/* Invoking callActivityOnResume(android.app.Activity) affects the activity in a way similar to
		 * callActivityOnPause. The activity's onResume() method is invoked instead of manipulating the
		 * activity's UI to force it to resume. */

		assertNotNull(testLocMgr);
		assertEquals(TEST_PROVIDER, testLocMgr.getProvider("TestProvider"));
	}

	public void testSearch()
	{
		Button searchbutton = solo.getButton(0);
		assertNotNull(searchbutton);
		AutoCompleteTextView tv = (AutoCompleteTextView) solo.getView(ch.eonum.R.id.searchforWhere);
		assertNotNull("TextView is null", tv);
		solo.typeText(0, "Tscharnerstrasse 41 3007 Bern");
		solo.clickOnButton(1);
		//TODO: Test MapView
	}
}
