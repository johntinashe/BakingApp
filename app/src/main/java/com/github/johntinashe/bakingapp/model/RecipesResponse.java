package com.github.johntinashe.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class RecipesResponse implements Parcelable{

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;
    @SerializedName("steps")
    private ArrayList<Step> steps;


    protected RecipesResponse(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
    }

    public static final Creator<RecipesResponse> CREATOR = new Creator<RecipesResponse>() {
        @Override
        public RecipesResponse createFromParcel(Parcel in) {
            return new RecipesResponse(in);
        }

        @Override
        public RecipesResponse[] newArray(int size) {
            return new RecipesResponse[size];
        }
    };

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
    }
}
