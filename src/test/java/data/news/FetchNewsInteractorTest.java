package data.news;

import usecase.fetch_news.FetchNewsInputData;
import usecase.fetch_news.FetchNewsInteractor;

public class FetchNewsInteractorTest {

    public static void main(String[] args) {

        // 1. 使用 Mock DAO，读取本地 sample JSON
        String sampleFilePath = "src/main/resources/sample_news.json";
        MockNewsDAO mockDao = new MockNewsDAO(sampleFilePath);

        // 2. 使用测试 Presenter
        TestPresenter presenter = new TestPresenter();

        // 3. 创建 Interactor
        FetchNewsInteractor interactor = new FetchNewsInteractor(mockDao, presenter);

        // 4. 构造 InputData（此 Use Case 没有额外参数，可传 null 或默认对象）
        FetchNewsInputData inputData = new FetchNewsInputData();

        // 5. 执行 Interactor
        interactor.execute(inputData);

        // 输出会在 TestPresenter 中打印到控制台
    }
}
