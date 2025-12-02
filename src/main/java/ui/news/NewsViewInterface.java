package ui.news;

public interface NewsViewInterface {

    /**
     *  Update the view after the user click the mouse.
     *  @param viewModel is the one to be updated. */
    void updateView(NewsViewModel viewModel);

    /**
     *  Show error message when failed ot present the news.
     *  @param message is the error reason. */
    void showError(String message);
}
