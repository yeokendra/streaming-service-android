package com.nontivi.nonton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import com.nontivi.nonton.common.TestComponentRule;
import com.nontivi.nonton.common.TestDataFactory;
import com.nontivi.nonton.data.model.response.NamedResource;
import com.nontivi.nonton.data.model.response.Pokemon;
import com.nontivi.nonton.features.main.MainActivity;
import com.nontivi.nonton.util.ErrorTestUtil;
import io.reactivex.Single;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private final TestComponentRule componentRule =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    private final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public TestRule chain = RuleChain.outerRule(componentRule).around(mainActivityTestRule);

    @Test
    public void checkPokemonsDisplay() {
        List<NamedResource> namedResourceList = TestDataFactory.makeNamedResourceList(5);
        List<String> pokemonList = TestDataFactory.makePokemonNameList(namedResourceList);
        stubDataManagerGetPokemonList(Single.just(pokemonList));
        mainActivityTestRule.launchActivity(null);

        for (NamedResource pokemonName : namedResourceList) {
            onView(withText(pokemonName.name)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void clickingPokemonLaunchesDetailActivity() {
        List<NamedResource> namedResourceList = TestDataFactory.makeNamedResourceList(5);
        List<String> pokemonList = TestDataFactory.makePokemonNameList(namedResourceList);
        stubDataManagerGetPokemonList(Single.just(pokemonList));
        stubDataManagerGetPokemon(Single.just(TestDataFactory.makePokemon("id")));
        mainActivityTestRule.launchActivity(null);

        onView(withText(pokemonList.get(0))).perform(click());

        onView(withId(R.id.image_pokemon)).check(matches(isDisplayed()));
    }

    @Test
    public void checkErrorViewDisplays() {
        stubDataManagerGetPokemonList(Single.error(new RuntimeException()));
        mainActivityTestRule.launchActivity(null);
        ErrorTestUtil.checkErrorViewsDisplay();
    }

    public void stubDataManagerGetPokemonList(Single<List<String>> single) {
        when(componentRule.getMockApiManager().getPokemonList(anyInt())).thenReturn(single);
    }

    public void stubDataManagerGetPokemon(Single<Pokemon> single) {
        when(componentRule.getMockApiManager().getPokemon(anyString())).thenReturn(single);
    }
}
