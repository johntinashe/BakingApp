package com.github.johntinashe.bakingapp;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johntinashe.bakingapp.model.Ingredient;
import com.github.johntinashe.bakingapp.model.RecipesResponse;
import com.github.johntinashe.bakingapp.model.Step;
import com.github.johntinashe.bakingapp.widget.RecipeIngWidget;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@SuppressWarnings("ALL")
public class RecipeListActivity extends AppCompatActivity {

    private static final String PREFERENCES_ID = "ID" ;
    private static final String WIDGET_TITLE = "TITLE" ;
    private static final String WIDGET_CONTENT = "CONTENT";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @BindView(R.id.recipe_list) RecyclerView mRecyclerView;
    @BindView(R.id.ingredients_tv) TextView mIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;

        RecipesResponse response;

        Intent i = getIntent();
        if (i == null) {
            error();
        } else {
            response = i.getParcelableExtra("recipe");
            if (response != null) {
                setTitle(response.getName());
                showSteps(response.getSteps());
                addWidget(response.getName(),ingredientsString(response.getIngredients()),response.getId());
                mIngredients.setText(ingredientsString(response.getIngredients()));
            }
        }
    }

    private void addWidget(String title,String ingredients,int id) {

        SharedPreferences sharedPreferences = getSharedPreferences("WIDGET", MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(PREFERENCES_ID, id)
                .putString(WIDGET_TITLE, title)
                .putString(WIDGET_CONTENT, ingredients)
                .apply();

    ComponentName provider = new ComponentName(this, RecipeIngWidget.class);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] ids = appWidgetManager.getAppWidgetIds(provider);
    RecipeIngWidget bakingWidgetProvider = new RecipeIngWidget();
            bakingWidgetProvider.onUpdate(this, appWidgetManager, ids);
    }

    private String ingredientsString(List<Ingredient> ingredients){
        StringBuilder result = new StringBuilder();
        for (Ingredient ingredient :  ingredients){
            result.append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append(" ").append(ingredient.getIngredient()).append("\n");
        }
        return result.toString();
    }

    private void error() {
        finish();
        Toast.makeText(this, R.string.error_no_details, Toast.LENGTH_SHORT).show();
    }

    private void showSteps(ArrayList<Step> steps) {

        SlimAdapter slimAdapter = SlimAdapter.create()
                .register(R.layout.step_single_item, new SlimInjector<Step>() {
                    @Override
                    public void onInject(@NonNull final Step data, @NonNull IViewInjector injector) {
                        injector.with(R.id.step_short_desc, new IViewInjector.Action<TextView>() {
                            @Override
                            public void action(TextView view) {
                                view.setText(data.getShortDescription());
                            }
                        }).with(R.id.play_btn, new IViewInjector.Action<ImageView>() {
                            @Override
                            public void action(ImageView view) {
                                assert data.getVideoURL() != null;
                                if (!data.getVideoURL().equals("")) {
                                    view.setVisibility(View.VISIBLE);
                                }
                            }
                        }).with(R.id.step_number, new IViewInjector.Action<TextView>() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void action(TextView view) {
                                view.setText(String.format("%d", data.getId()));
                            }
                        }).clicked(R.id.step_item, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    if (mTwoPane) {
                                        Bundle arguments = new Bundle();
                                        arguments.putParcelable("step",data);
                                        RecipeDetailFragment fragment = new RecipeDetailFragment();
                                        fragment.setArguments(arguments);
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.recipe_detail_container, fragment)
                                                .commit();
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
                                        intent.putExtra("step", data);
                                        startActivity(intent);
                                    }

                            }
                        });
                    }
                })
                .enableDiff()
                .attachTo(mRecyclerView);
        slimAdapter.updateData(steps);

    }

}
