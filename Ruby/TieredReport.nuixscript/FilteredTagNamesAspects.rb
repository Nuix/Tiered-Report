# This file allows you to define specialized versions of the "Tags" aspect in which you provide a callback
# which will be provided the tags for each item as the aspect is collecting data on it.  The callback can
# then modify individual tags, exclude tags or even add new ones.  This is useful in instances where you
# want to report tags but have a very specific set of tags or tags which may include information that you
# want to normalize.

# Below is an example which filters the tags reported to those which match some criteria.  These tags
# also are nested under a date tag, but in the report we want them reported together regardless of the
# date, so we trim off the leading date from the tag name where prsent.  Finally we yield back to the
# aspect our modified collection of tag values to report on.

=begin

com.nuix.tieredreport.aspects.ItemAspectFactory.addFilteredTagNamesAspect("Lexicon/Watchlist/Workflow Tags") do |item,item_tags|
	# Possible example tags:
	#
	# DATE|Watchlist|NAME => keep, remove date
	# DATE|Lexicon|NAME   => keep, remove date
	# Workflow|NAME       => keep
	# DATE|Batch|NAME     => exclude

	# Keep only the tags we want
	item_tags = item_tags.select{|tag| tag =~ /20[0-9]{6}\|Watchlist\|.*/i || tag =~ /20[0-9]{6}\|Lexicon\|.*/i || tag =~ /Workflow\|.*/i }

	# Remove date part of tags
	item_tags = item_tags.map{|tag| tag.gsub(/20[0-9]{6}\|/,"") }

	# Finally make sure we give back our updated tags
	# Note: When I was testing this 'yield' and 'return' keywords were
	# throwing error here, since the variable alone is implicitly the
	# same thing that seems to work for some reason.  This might be something
	# to do with JRuby mapping this to a BiFunction?
	item_tags
end

=end