package es.ubu.lsi.ubumonitor.view.chart.logs;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class ViolinLogTime extends BoxplotLogTime{

	public ViolinLogTime(MainController mainController) {
		super(mainController, ChartType.VIOLIN_LOG_TIME);
	}
	
	@Override
	public JSObject getOptions(JSObject jsObject) {
		
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalViolin" : "violin");
		jsObject.put("tooltipDecimals", 0);
		String xLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{suggestedMax:" + getSuggestedMax(textFieldMax.getText())
				+ ",stepSize:0}}],xAxes:[{" + xLabel + "}]}");

		return jsObject;
	}

}
