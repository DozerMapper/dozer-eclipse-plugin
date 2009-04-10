package net.sf.dozer.eclipse.plugin.editorpage.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ufacekit.core.databinding.instance.observable.ILazyObserving;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract public class LocatedLazyObserving implements ILazyObserving {

	/**
	 * According to the DTD/XSD this method finds an existent Element that is
	 * placed after the position that newChild will have. It is used for
	 * the insertBefore() method from Node.
	 * 
	 * @param parent The Node that will get newChild as a new child node
	 * @param newChild The Node that wants to be appended to parent
	 * @return the element that is placed after newChild or null if newChild can be appended at last position
	 */
	protected Node getNextExistentElement(Element parent, Element newChild) {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
		List<CMNode> cmNodes = getAvailableChildrenAtIndex(parent, 0, 0);
		
		//get CMNode representation of newChild
		CMNode newChildCmNode = null;
		for (CMNode cmNode : cmNodes) {
			String curModelNodeName = cmNode.getNodeName();
			
			if (curModelNodeName.equals(newChild.getNodeName())) {
				newChildCmNode = cmNode;
				break;
			}
		}
		
		//CMNode had been found, check all existent elements if newChild can be inserted before them
		if (newChildCmNode != null) {
			NodeList nodeList = parent.getChildNodes();
			int len = nodeList.getLength();
			for (int i=0; i<len; i++) {
				try {
					if (modelQuery.canInsert(parent, newChildCmNode, i, ModelQuery.VALIDITY_STRICT)) {
						return nodeList.item(i);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;					
	}
	
	
	private List<CMNode> getAvailableChildrenAtIndex(Element parent, int index, int validityChecking) {
		List<CMNode> list = null;
		try {
			list = new ArrayList<CMNode>();
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
			CMElementDeclaration parentDecl = modelQuery.getCMElementDeclaration(parent);
			list = modelQuery.getAvailableContent(parent, parentDecl, ModelQuery.INCLUDE_CHILD_NODES);
		} catch (Exception e) {
			Logger.logException("Cannot query model. Missing WTP DTD or XSD Plugin?", e);
		}
		return list;
	}	

}
