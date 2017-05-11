package com.kickstarter.ui.viewholders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kickstarter.R;
import com.kickstarter.libs.KSString;
import com.kickstarter.libs.transformations.CircleTransformation;
import com.kickstarter.libs.utils.ObjectUtils;
import com.kickstarter.models.Project;
import com.kickstarter.models.SurveyResponse;
import com.kickstarter.viewmodels.UnansweredSurveyHolderViewModel;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.kickstarter.libs.rx.transformers.Transformers.observeForUI;

public class UnansweredSurveyViewHolder extends KSViewHolder {

  private final KSString ksString;
  @Bind(R.id.survey_avatar_image) ImageView creatorAvatarImageView;
  @Bind(R.id.survey_title) TextView surveyTitleTextView;
  @Bind(R.id.survey_text) TextView surveyTextView;

  @BindString(R.string.Creator_name_needs_some_information_to_deliver_your_reward_for_project_name) String surveyDescriptionString;

  private final UnansweredSurveyHolderViewModel.ViewModel viewModel;

  private final Delegate delegate;

  public interface Delegate {
    void surveyClicked(UnansweredSurveyViewHolder viewHolder, SurveyResponse surveyResponse);
  }

  public UnansweredSurveyViewHolder(final View view, final Delegate delegate) {
    super(view);
    this.delegate = delegate;
    this.viewModel = new UnansweredSurveyHolderViewModel.ViewModel(environment());
    this.ksString = environment().ksString();

    ButterKnife.bind(this, view);

    this.viewModel.outputs.creatorAvatarImage()
      .compose(bindToLifecycle())
      .compose(observeForUI())
      .subscribe(this::setCreatorAvatarImage);

    this.viewModel.outputs.creatorName()
      .compose(bindToLifecycle())
      .compose(observeForUI())
      .subscribe(this.surveyTitleTextView::setText);

    this.viewModel.outputs.projectForSurveyDescription()
      .compose(bindToLifecycle())
      .compose(observeForUI())
      .subscribe(this::setSurveyDescription);
  }

  private void setSurveyDescription(final @NonNull Project projectForSurveyDescription) {
    this.surveyTextView.setText(Html.fromHtml(
      ksString.format(
        surveyDescriptionString,
        "creator_name", projectForSurveyDescription.creator().name(),
        "project_name", projectForSurveyDescription.name()
      )));
  }

  @Override
  public void bindData(final @Nullable Object data) throws Exception {
    final SurveyResponse configData = ObjectUtils.requireNonNull(
      (SurveyResponse) data
    );
    this.viewModel.inputs.configureWith(configData);
  }

  private void setCreatorAvatarImage(final @NonNull String creatorAvatarImage) {
    Picasso.with(context()).load(creatorAvatarImage).transform(new CircleTransformation()).into(this.creatorAvatarImageView);
  }

  @Override
  public void onClick(final @NonNull View view) {
    this.viewModel.inputs.surveyClicked();
  }
}
