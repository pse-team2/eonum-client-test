package ch.eonum.test;

import ch.eonum.StartActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import com.jayway.android.robotium.solo.Solo;

public class HealthTest extends ActivityInstrumentationTestCase2<StartActivity>{

	private Solo solo;

	public HealthTest() {
		super("ch.eonum", StartActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}


	@Smoke
	public void testAboutActivity() throws Exception {
		// Assert HealthActivity
		solo.assertCurrentActivity("Expected StartActivity activity", "StartActivity"); 
		solo.clickOnMenuItem("About");
		
		// Assert About Activity
		solo.assertCurrentActivity("Expected About activity", "About");
		
		// Assert that text is found
		assertTrue("Text 'Arztsuche' not found", solo.searchText("Arztsuche")); 
		assertTrue("Text 'eonum' not found", solo.searchText("eonum")); 
	}
	
	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
