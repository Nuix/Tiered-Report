# Use this file to define basic custom aspects to be registerd with
# the ItemAspectFactory (and therefore usable in reports).  The workflow
# looks like this:
#
# ItemAspectFactory.registerBasicScriptableAspect(aspectName,aspectReportLabel,valueFunction)
#
# aspectName - Name of this aspect in the settings dialog
# aspectReportLabel - Label of this aspect in generated reports
# valueFunction - Given an item and the case containing it, yields back a value to classify
#                 the item, return nil to report nothing for an item
#
# Examples:
#
# ItemAspectFactory.registerBasicScriptableAspect("Item Name Length","Name Length Chars") do |nuix_case,item|
# 	next item.getLocalisedName.size
# end

# ItemAspectFactory.registerBasicScriptableAspect("Descendant Count","Descendant Count") do |nuix_case,item|
# 	next item.getDescendants.size
# end

# ItemAspectFactory.registerBasicScriptableAspect("Child Count","Child Count") do |nuix_case,item|
# 	next item.getChildren.size
# end