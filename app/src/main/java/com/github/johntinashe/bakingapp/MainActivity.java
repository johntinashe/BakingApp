package com.github.johntinashe.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.johntinashe.bakingapp.model.RecipesResponse;
import com.github.johntinashe.bakingapp.utils.SimpleIdlingResource;
import com.github.johntinashe.bakingapp.viewmodels.MainViewModel;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity {

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getMovies().observe(this, new Observer<ArrayList<RecipesResponse>>() {
            @Override
            public void onChanged(@Nullable ArrayList<RecipesResponse> recipesResponses) {
                loadRecipes(recipesResponses);
                getIdlingResource();
            }
        });
    }


    private void loadRecipes(ArrayList<RecipesResponse> recipes) {

        SlimAdapter slimAdapter = SlimAdapter.create()
                .register(R.layout.recipe_item, new SlimInjector<RecipesResponse>() {
                    @Override
                    public void onInject(@NonNull final RecipesResponse data, @NonNull IViewInjector injector) {
                        injector.with(R.id.recipe_name, new IViewInjector.Action<TextView>() {
                            @Override
                            public void action(TextView view) {
                                view.setText(data.getName());
                            }
                        }).clicked(R.id.recipe_item, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), RecipeListActivity.class);
                                intent.putExtra("recipe", data);
                                startActivity(intent);
                            }
                        }).with(R.id.view, new IViewInjector.Action<View>() {
                            @Override
                            public void action(View view) {
                                ColorGenerator generator = ColorGenerator.MATERIAL;
                                int color = generator.getColor(data.getName().charAt(0));
                                view.setBackgroundColor(color);
                            }
                        });
                    }
                })
                .enableDiff()
                .attachTo(mRecyclerView);
        slimAdapter.updateData(recipes);
    }
}
