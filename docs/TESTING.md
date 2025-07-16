# Testing Documentation

This document describes the comprehensive testing infrastructure implemented for the Kids POS Android project.

## Test Structure

### Unit Tests (`app/src/test/`)

Unit tests are located in the `test` source set and test individual components in isolation.

#### ViewModels
- `MainViewModelTest` - Tests for the main activity view model
- `ItemListViewModelTest` - Tests for item list functionality
- `CalculatorDialogViewModelTest` - Tests for calculator/payment logic
- `StoreListViewModelTest` - Tests for store selection

#### API
- `APIServiceTest` - Tests API service with MockWebServer

#### Test Utilities
- `CoroutineTestRule` - JUnit rule for coroutine testing
- `FakeEventBus` - Fake implementation of EventBus for testing
- `FakeGlobalConfig` - Fake configuration for testing
- `LiveDataTestUtil` - Extension functions for testing LiveData

### Instrumented Tests (`app/src/androidTest/`)

UI tests using Espresso that run on an Android device or emulator.

- `MainActivityTest` - Tests main activity UI interactions
- `ItemListFragmentTest` - Tests item list fragment
- `LaunchActivityTest` - Tests launch screen functionality
- `EspressoTestUtil` - Helper functions for UI tests

## Running Tests

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests for specific variant
./gradlew testProdDebugUnitTest
./gradlew testDemoDebugUnitTest

# Run with coverage
./gradlew jacocoTestReport
```

### Instrumented Tests
```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest

# Run for specific variant
./gradlew connectedProdDebugAndroidTest
```

### Code Coverage
```bash
# Generate coverage report
./gradlew jacocoTestReport

# Check coverage thresholds
./gradlew jacocoTestCoverageVerification
```

Coverage reports are generated at:
- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

## Coverage Requirements

- Overall coverage: 70%
- Per-class line coverage: 60% (excluding entities, DI, and generated code)

## CI/CD Integration

### GitHub Actions Workflows

1. **Android CI** (`android-ci.yml`)
   - Runs on push to main/develop/feat-* branches
   - Executes lint checks, unit tests, and builds APKs
   - Runs instrumented tests on multiple API levels (26, 29, 33)
   - Uploads test results and coverage to Codecov

2. **PR Checks** (`pr-checks.yml`)
   - Runs on pull requests
   - Performs code quality checks (Detekt, ktlint)
   - Checks APK size constraints
   - Runs Danger for automated PR review

3. **Dependency Updates** (`dependabot.yml`)
   - Weekly automated dependency updates
   - Separate configurations for Gradle and GitHub Actions

## Best Practices

1. **Test Naming**: Use descriptive names with backticks for readability
   ```kotlin
   @Test
   fun `should return error when network fails`() { }
   ```

2. **Test Structure**: Follow AAA pattern
   ```kotlin
   @Test
   fun testExample() {
       // Arrange (Given)
       val input = "test"
       
       // Act (When)
       val result = someFunction(input)
       
       // Assert (Then)
       assertEquals("expected", result)
   }
   ```

3. **Coroutine Testing**: Use `runTest` for suspend functions
   ```kotlin
   @Test
   fun `test coroutine function`() = runTest {
       // Test implementation
   }
   ```

4. **LiveData Testing**: Use `getOrAwaitValue()` extension
   ```kotlin
   val result = viewModel.someData.getOrAwaitValue()
   assertEquals(expected, result)
   ```

## Troubleshooting

### Common Issues

1. **Flaky UI Tests**: Use `EspressoTestUtil.waitFor()` for timing issues
2. **Coroutine Test Failures**: Ensure `CoroutineTestRule` is applied
3. **MockWebServer Issues**: Check proper setup/teardown in @Before/@After

### Debug Tips

- Run tests with `--info` flag for detailed output
- Use `@RunWith(AndroidJUnit4::class)` for instrumented tests
- Check Logcat for detailed error messages during UI tests