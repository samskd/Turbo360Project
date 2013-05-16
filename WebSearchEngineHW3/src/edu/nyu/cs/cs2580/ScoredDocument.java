package edu.nyu.cs.cs2580;

/**
 * Document with score.
 * 
 * @author fdiaz
 * @author congyu
 */
class ScoredDocument implements Comparable<ScoredDocument> {
  private Document _doc;
  private double _score;

  public ScoredDocument(Document doc, double score) {
	    _doc = doc;
	    _score = score;
	  }

  public Document getDocument(){
	 return _doc;
  }
  
  public String asTextResult() {
    StringBuffer buf = new StringBuffer();
    buf.append(_doc._docid).append("\t");
    if(_doc._isRealtime)
    	buf.append("<a href='"+_doc.getUrl()+"'>"+_doc.getTitle()+"</a>").append("\t");
    else
    	buf.append(_doc.getTitle()).append("\t");
    //buf.append(_doc.getPageRank()).append("\t");
    //buf.append(_doc.getNumViews()).append("\t");
    buf.append(_score);
    return buf.toString();
  }

  /**
   * @CS2580: Student should implement {@code asHtmlResult} for final project.
   */
  public String asHtmlResult() {
	  
	  String url;
	  
	  if(_doc._isRealtime){
		  url = _doc.getUrl();
	  }else{
		  url = "file://localhost/Users/samitpatel/Desktop/Turbo360Project/WebSearchEngineHW3/data/wiki/"+_doc.getUrl();
	  }
	  
	  return "<div>" +
				"<span style='font-size:20px'>" +
				"<a href='"+url+"' target='_blank'>"+_doc.getTitle()+"</a>" +
				"</span><br>" +
				"<span>Score : "+Double.toString(_score)+"</span>" +
			"</div><br><br>";
  }

  @Override
  public int compareTo(ScoredDocument o) {
    if (this._score == o._score) {
      return 0;
    }
    return (this._score > o._score) ? 1 : -1;
  }
}
