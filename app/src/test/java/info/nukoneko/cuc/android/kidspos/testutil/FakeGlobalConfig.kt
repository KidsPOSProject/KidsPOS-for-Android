package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.util.Mode

/**
 * Fake implementation of GlobalConfig for testing
 */
class FakeGlobalConfig : GlobalConfig {
    private var _currentStore: Store? = null
    private var _currentStaff: Staff? = null
    private var _currentServerAddress: String = "http://localhost:8080"
    private var _currentRunningMode: Mode = Mode.PRACTICE
    
    override var currentStore: Store?
        get() = _currentStore
        set(value) {
            _currentStore = value
        }
    
    override var currentStaff: Staff?
        get() = _currentStaff
        set(value) {
            _currentStaff = value
        }
    
    override var currentServerAddress: String
        get() = _currentServerAddress
        set(value) {
            _currentServerAddress = value
        }
    
    override var currentRunningMode: Mode
        get() = _currentRunningMode
        set(value) {
            _currentRunningMode = value
        }
    
    // Test helper methods
    fun setTestStore(store: Store?) {
        _currentStore = store
    }
    
    fun setTestStaff(staff: Staff?) {
        _currentStaff = staff
    }
    
    fun setTestServerAddress(address: String) {
        _currentServerAddress = address
    }
    
    fun setTestRunningMode(mode: Mode) {
        _currentRunningMode = mode
    }
}