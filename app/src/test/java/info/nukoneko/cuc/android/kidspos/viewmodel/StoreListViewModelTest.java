package info.nukoneko.cuc.android.kidspos.viewmodel;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import info.nukoneko.cuc.android.kidspos.BuildConfig;
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StoreListViewModelTest {

    private KidsPOSApplication application;

    @Before
    public void setUp() {
        application = (KidsPOSApplication) RuntimeEnvironment.application;
    }
}
