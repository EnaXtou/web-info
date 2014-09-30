package cz.plsi.webInfo.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class Page extends Widget {
	private final DivElement contentElement;
	private int pageIndex;
	
	public Page() {
		Element myElem = getElement();
		contentElement = Document.get().createDivElement();
	    myElem.appendChild(contentElement);
	}
	
	  public DivElement getContentContainer() {
	    return contentElement;
	  }

	  public int getPageIndex() {
	    return pageIndex;
	  }

	  public void setPageIndex(int index) {
	    this.pageIndex = index;
	  }

	  public void setWidth(int newWidth) {
	    getElement().getStyle().setPropertyPx("width", newWidth);
	  }
}
