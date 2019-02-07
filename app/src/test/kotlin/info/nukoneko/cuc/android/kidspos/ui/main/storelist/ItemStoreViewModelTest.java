package info.nukoneko.cuc.android.kidspos.ui.main.storelist;

import android.databinding.Observable;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import info.nukoneko.cuc.android.kidspos.entity.Store;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class ItemStoreViewModelTest {

    @Test
    public void shouldGetStoreName() {
        Store store = new Gson().fromJson("{\"id\": 1, \"name\": \"ぬいぐるみ\"}", Store.class);

        ItemStoreListContentViewModel viewModel = new ItemStoreListContentViewModel(store, null);
        assertEquals(store.getName(), viewModel.getStoreName());
    }

    @Test
    public void shouldNotifyPropertyChangeWhenSetStore() {
        Store store = new Gson().fromJson("{\"id\": 1, \"name\": \"ぬいぐるみ\"}", Store.class);

        ItemStoreListContentViewModel viewModel = new ItemStoreListContentViewModel(store, null);
        Observable.OnPropertyChangedCallback mockCallback =
                mock(Observable.OnPropertyChangedCallback.class);
        viewModel.addOnPropertyChangedCallback(mockCallback);

        viewModel.setStore(store);
        verify(mockCallback).onPropertyChanged(any(Observable.class), anyInt());
    }
}
