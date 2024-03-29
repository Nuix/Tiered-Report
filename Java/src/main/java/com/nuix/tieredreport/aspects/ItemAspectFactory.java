package com.nuix.tieredreport.aspects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import nuix.Case;
import nuix.Item;

/***
 * Class which can provide one of every available item apsect.  Used to populate list of item
 * aspects in settings dialog for user to pick from.
 * @author Jason Wells
 *
 */
public class ItemAspectFactory {
	private static List<ItemAspect> aspects = new ArrayList<ItemAspect>();
	static {
		aspects.add(new CaseGUIDAspect());
		aspects.add(new CaseNameAspect());
		aspects.add(new CaseLocationAspect());
		aspects.add(new EvidenceNameAspect());
		aspects.add(new CustodianNameAspect());
		aspects.add(new BatchLoadIDAspect());
		aspects.add(new BatchLoadDateAspect());
		aspects.add(new MaterialStatusAspect());
		aspects.add(new DeletedStatusAspect());
		aspects.add(new EncryptedStatusAspect());
		aspects.add(new DigestListAspect());
		aspects.add(new IrregularCategoriesAspect());
		aspects.add(new ExclusionNameAspect());
		aspects.add(new ItemCategoryAspect());
		aspects.add(new ItemKindAspect());
		aspects.add(new TopLevelItemKindAspect());
		aspects.add(new ItemTypeAspect());
		aspects.add(new TopLevelItemTypeAspect());
		aspects.add(new MimeTypeAspect());
		aspects.add(new OriginalExtensionAspect());
		aspects.add(new CorrectedExtensionAspect());
		aspects.add(new ItemDateYearAspect());
		aspects.add(new ItemDateMonthAspect());
		aspects.add(new ItemDateYearMonthAspect());
		aspects.add(new ItemDateYearMonthWithZerosAspect());
		aspects.add(new ItemDateFullAspect());
		aspects.add(new TopLevelItemDateYearAspect());
		aspects.add(new TopLevelItemDateMonthAspect());
		aspects.add(new TopLevelItemDateYearMonthAspect());
		aspects.add(new TopLevelItemDateYearMonthWithZerosAspect());
		aspects.add(new TopLevelItemDateFullAspect());
		aspects.add(new ItemSetNamesAspect());
		aspects.add(new ItemSetNameAndDuplicatStatus());
		aspects.add(new GlobalItemSetDuplicateStatusAspect());
		aspects.add(new ProductionSetAspect());
		aspects.add(new LanguageAspect());
		aspects.add(new NamedEntityAspect());
		aspects.add(new PhysicalFileNameAspect());
		aspects.add(new PropertyNamesAspect());
		aspects.add(new RecipientAddressesAspect());
		aspects.add(new ToAddressesAspect());
		aspects.add(new CcAddressesAspect());
		aspects.add(new BccAddressesAspect());
		aspects.add(new RecipientDomainsAspect());
		aspects.add(new SenderAddressesAspect());
		aspects.add(new SenderDomainsAspect());
		aspects.add(new ToDomainsAspect());
		aspects.add(new CcDomainsAspect());
		aspects.add(new BccDomainsAspect());
		aspects.add(new TagNamesAspect());
		aspects.add(new TermsAspect());
	}
	
	public static List<ItemAspect> getAspects(){
		return aspects;
	}
	
	public static void addFilteredTagNamesAspect(String name, BiFunction<Item,List<String>,List<String>> tagModifierFunction) {
		aspects.add(new FilteredTagNamesAspect(name, tagModifierFunction));
	}
	
	public static void registerBasicScriptableAspect(String aspectName, String aspectReportLabel, BiFunction<Case,Item,Object> valueFunction) {
		aspects.add(new BasicScriptableAspect(aspectName,aspectReportLabel,valueFunction));
	}
}
