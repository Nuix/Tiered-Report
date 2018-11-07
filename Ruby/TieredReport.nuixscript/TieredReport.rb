# Menu Title: Tiered Report
# Needs Case: false
# Needs Selected Items: false

script_directory = File.dirname(__FILE__)
require File.join(script_directory,"Nx.jar")
java_import "com.nuix.nx.NuixConnection"
java_import "com.nuix.nx.LookAndFeelHelper"
java_import "com.nuix.nx.dialogs.ChoiceDialog"
java_import "com.nuix.nx.dialogs.TabbedCustomDialog"
java_import "com.nuix.nx.dialogs.CommonDialogs"
java_import "com.nuix.nx.dialogs.ProgressDialog"
java_import "com.nuix.nx.controls.models.Choice"
require File.join(script_directory,"TieredReport.jar")
java_import "com.nuix.tieredreport.TieredReportData"
java_import "com.nuix.tieredreport.ReportSheetInfo"
java_import "com.nuix.tieredreport.ReportGenerator"
java_import "com.nuix.tieredreport.aspects.ItemAspectFactory"
java_import "com.nuix.tieredreport.aspects.TermsAspect"
java_import "com.nuix.tieredreport.aspects.CustomMetadataAspect"
java_import "com.nuix.tieredreport.aspects.PropertyMetadataAspect"
java_import "com.nuix.tieredreport.FileSizeUnit"
java_import "org.joda.time.DateTime"

LookAndFeelHelper.setWindowsIfMetal
NuixConnection.setUtilities($utilities)
NuixConnection.setCurrentNuixVersion(NUIX_VERSION)

require 'json'

# Swing label hacky fix
def build_hacky_label(label,is_bold)
	if is_bold
		return "<html><body><div style=\"width:150px;text-align:left\"><b>#{label}</b></div></body></html>"
	else
		return "<html><body><div style=\"width:150px;text-align:left\">#{label}</div></body></html>"
	end
end

# Load aspects
item_aspects = ItemAspectFactory.getAspects
# Generate any custom metadata related aspects user may have specified
custom_metadata_aspect_settings_file = File.join(script_directory,"CustomMetadataAspects.json")
if java.io.File.new(custom_metadata_aspect_settings_file).exists
	custom_metadata_aspect_settings = JSON.parse(File.read(custom_metadata_aspect_settings_file))
	custom_metadata_aspect_settings.each do |cm_setting|
		if cm_setting["enabled"] == true
			item_aspects << CustomMetadataAspect.new(cm_setting["field_name"],cm_setting["report_label"],cm_setting["value_if_missing"])
		end
	end
end
# Generate any property metadata related aspects user may have specified
property_metadata_aspect_settings_file = File.join(script_directory,"PropertyMetadataAspects.json")
if java.io.File.new(property_metadata_aspect_settings_file).exists
	property_metadata_aspect_settings = JSON.parse(File.read(property_metadata_aspect_settings_file))
	property_metadata_aspect_settings.each do |p_setting|
		if p_setting["enabled"] == true
			item_aspects << PropertyMetadataAspect.new(p_setting["property_name"],p_setting["report_label"],p_setting["value_if_missing"])
		end
	end
end

report_sheets = 15

dialog = TabbedCustomDialog.new("Tiered Report")
dialog.setTabPlacementLeft

month_start = DateTime.new.dayOfMonth.withMinimumValue
month_end = DateTime.new.dayOfMonth.withMaximumValue

# Main Tab
main_tab = dialog.addTab("main_tab",build_hacky_label("Main",false))
main_tab.appendSaveFileChooser("report_file","Report XLSX File","Excel 2007 Workbook","xlsx")
# We don't want the report file name to be saved to prevent saving overwriting previous reports
main_tab.doNotSerialize("report_file")
if !$current_case.nil?
	file_timestamp = Time.now.strftime("%Y%m%d_%H%M%S")
	report_path = File.join($current_case.getLocation.getPath,"Reports","TieredReport_#{file_timestamp}.xlsx")
	report_path = report_path.gsub(/[\\\/]/,java.io.File.separator)
	main_tab.setText("report_file",report_path)
end
main_tab.appendCheckBox("report_excluded","Report Excluded Items as Well",false)
main_tab.appendCheckBox("use_si_units","Report Sizes using SI Units (1 GB = 1000^3 Bytes)",true)
main_tab.appendCheckBox("save_report_settings","Save settings JSON file with report",true)
main_tab.appendCheckBox("open_report","Open Report on Completion",true)
main_tab.appendCheckBox("date_range_filter_batches","Only Items in Batch Loads in Date Range",false)
main_tab.appendDatePicker("batch_load_min_date","Batch Load Date Min",month_start)
main_tab.appendDatePicker("batch_load_max_date","Batch Load Date Max",month_end)
main_tab.appendButton("set_range_month","Set Range to This Month") do
	main_tab.setDate("batch_load_min_date",month_start)
	main_tab.setDate("batch_load_max_date",month_end)
end
main_tab.enabledOnlyWhenChecked("batch_load_min_date","date_range_filter_batches")
main_tab.enabledOnlyWhenChecked("batch_load_max_date","date_range_filter_batches")
main_tab.enabledOnlyWhenChecked("set_range_month","date_range_filter_batches")
main_tab.appendHeader("Scope Query (blank is all items)")
main_tab.appendTextArea("scope_query","","")

# Case Paths Tab
case_paths_tab = dialog.addTab("case_paths_tab",build_hacky_label("Case Paths",false))
case_paths_tab.appendRadioButton("use_current_case","Use Current Case","case_source_group",false)
case_paths_tab.appendRadioButton("use_case_paths","Use Case Paths","case_source_group",true)
case_paths_tab.appendCheckBox("allow_migration","Migrate cases as needed",true)
case_paths_tab.appendPathList("case_paths")
case_paths_tab.getControl("case_paths").setFilesButtonVisible(false)
case_paths_tab.enabledOnlyWhenChecked("case_paths","use_case_paths")
case_paths_tab.enabledOnlyWhenChecked("allow_migration","use_case_paths")
case_paths_tab.enabledOnlyWhenChecked("case_paths","use_case_paths")
if $current_case.nil?
	case_paths_tab.getControl("use_current_case").setEnabled(false)
	case_paths_tab.getControl("use_case_paths").setSelected(true)
else
	case_paths_tab.getControl("use_current_case").setSelected(true)
end

def generate_aspect_choices(aspects)
	return aspects.map do |ia|
		next Choice.new(ia,ia.getAspectName,ia.getAspectName)
	end
end

size_unit_choices = [
	"Giga Bytes",
	"Bytes",
	"Dynamic",
]

# Report Sheets Tabs
report_sheets.times do |sheet_index|
	sheet_index += 1
	# Following HTML label hacks are there to control tab label styling, mostly fixed width
	report_tab = dialog.addTab("sheet_#{sheet_index}",build_hacky_label("Sheet #{sheet_index}",false))
	report_tab.appendCheckBox("generate_sheet_#{sheet_index}","Generate this sheet",false)
	report_tab.appendTextField("sheet_name_#{sheet_index}","Sheet Name","Sheet #{sheet_index}")
	report_tab.getControl("generate_sheet_#{sheet_index}").addItemListener {
		checked = report_tab.getControl("generate_sheet_#{sheet_index}").isSelected
		name = report_tab.getControl("sheet_name_#{sheet_index}").getText
		dialog.setTabLabel("sheet_#{sheet_index}",build_hacky_label(name,checked))
	}

	report_tab.appendCheckBoxes("report_audited_count_#{sheet_index}","Report Total Audited Count",true,
		"report_audited_size_#{sheet_index}","Report Total Audited Size",true)

	report_tab.appendCheckBoxes("report_file_size_#{sheet_index}","Report Total File Size",true,
		"report_digest_input_size_#{sheet_index}","Report Total Digest Input Size",true)

	report_tab.appendCheckBoxes("report_corrupted_#{sheet_index}","Report Total Corrupted Count",false,
		"report_encrypted_#{sheet_index}","Report Total Encrypted Count",false)

	report_tab.appendCheckBoxes("report_deleted_#{sheet_index}","Report Total Deleted Count",false,
		"last_tier_count_sorted_#{sheet_index}","Sort Last Tier by Item Count Descending",false)

	report_tab.appendCheckBoxes("include_category_headers_#{sheet_index}","Include Category Header Rows",true,
		"use_sparse_columns_#{sheet_index}","Use Sparse Columns",false)

	report_tab.appendComboBox("size_unit_#{sheet_index}","Data Size Unit",size_unit_choices)

	report_tab.appendHeader("Sub Scope Query")
	report_tab.appendTextArea("sub_scope_query_#{sheet_index}","","")
	
	report_tab.appendHeader("Reported Item Aspects")
	report_tab.appendChoiceTable("sheet_aspects_#{sheet_index}","Tiers",generate_aspect_choices(item_aspects))
	
	report_tab.enabledOnlyWhenChecked("sheet_name_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("sheet_aspects_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("sub_scope_query_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_audited_count_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_audited_size_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_file_size_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_digest_input_size_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_corrupted_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_encrypted_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("report_deleted_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("last_tier_count_sorted_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("include_category_headers_#{sheet_index}","generate_sheet_#{sheet_index}")
	report_tab.enabledOnlyWhenChecked("use_sparse_columns_#{sheet_index}","generate_sheet_#{sheet_index}")
end

# Terms Tab
terms_tab = dialog.addTab("terms_tab",build_hacky_label("Terms",false))
terms_tab.appendHeader("Determine what terms will be used with terms aspect")
terms_tab.appendStringList("terms")

dialog.whenJsonFileLoaded do
	if $current_case.nil?
		case_paths_tab.getControl("use_case_paths").setSelected(true)
	end
end

dialog.validateBeforeClosing do |values|
	# Validate report file
	if values["report_file"].strip.empty?
		CommonDialogs.showWarning("Please select a valid path for 'Report XLSX File'")
		next false
	end

	if values["date_range_filter_batches"] && values["batch_load_max_date"].isBefore(values["batch_load_min_date"])
		CommonDialogs.showWarning("Invalid batch load date range.  Max date cannot be before min date.")
		next false
	end

	# Validate sheet settings
	all_sheets_valid = true
	sheet_error_message = ""
	sheets_to_generate = 0
	report_sheets.times do |sheet_index|
		sheet_index += 1
		if values["generate_sheet_#{sheet_index}"]
			sheets_to_generate += 1
			# Make sure at least one aspect is being reported in each sheet
			if values["sheet_aspects_#{sheet_index}"].size < 1
				sheet_error_message = "Sheet #{sheet_index}: Must select at least one item aspect to report."
				all_sheets_valid = false
				break
			end
			# Make sure each sheet has a name
			if values["sheet_name_#{sheet_index}"].strip.empty?
				sheet_error_message = "Sheet #{sheet_index}: Please provide a non-empty sheet name."
				all_sheets_valid = false
				break
			end
			# Make sure each sheet name does not exceed excel limit
			if values["sheet_name_#{sheet_index}"].size > 31
				sheet_error_message = "Sheet #{sheet_index}: Sheet name exceeds Excel 31 character limit."
				all_sheets_valid = false
				break
			end
		end
	end
	if !all_sheets_valid
		CommonDialogs.showWarning(sheet_error_message)
		next false
	end
	if sheets_to_generate < 1
		CommonDialogs.showWarning("You have not checked 'Generate Sheet' in any tab.")
		next false
	end

	# Validate case paths
	if values["use_case_paths"] && values["case_paths"].size < 1
		CommonDialogs.showWarning("Please provide at least one case path.")
		next false
	end

	next true
end

dialog.display
if dialog.getDialogResult == true
	start_time = Time.now
	values = dialog.toMap
	scope_query = values["scope_query"]

	if values["save_report_settings"]
		# Save settings used for this report along side the report
		saved_settings_file = java.io.File.new(values["report_file"].gsub(/\.xlsx/i,"\.json"))
		if !saved_settings_file.exists
			saved_settings_file.getParentFile.mkdirs
		end
		dialog.saveJsonFile(saved_settings_file.getPath)
	end

	date_range_filter_batches = values["date_range_filter_batches"]
	batch_load_min_date = DateTime.new(values["batch_load_min_date"])
	batch_load_max_date = DateTime.new(values["batch_load_max_date"])
		.withHourOfDay(23)
		.withMinuteOfHour(59)

	TermsAspect.setTerms(values["terms"])
	
	collect_audited_count = false
	collect_audited_sizes = false
	collect_file_sizes = false
	collect_digest_input_sizes = false
	collect_corrupted_count = false
	collect_encrypted_count = false
	collect_deleted_count = false
	distinct_aspects = {}
	report_sheets.times do |sheet_index|
		if values["generate_sheet_#{sheet_index}"]
			values["sheet_aspects_#{sheet_index}"].each do |aspect|
				distinct_aspects[aspect] = true
			end

			collect_audited_count = true if values["report_audited_count_#{sheet_index}"] == true
			collect_audited_sizes = true if values["report_audited_size_#{sheet_index}"] == true
			collect_file_sizes = true if values["report_file_size_#{sheet_index}"] == true
			collect_digest_input_sizes = true if values["report_digest_input_size_#{sheet_index}"] == true
			collect_corrupted_count = true if values["report_corrupted_#{sheet_index}"] == true
			collect_encrypted_count = true if values["report_encrypted_#{sheet_index}"] == true
			collect_deleted_count = true if values["report_deleted_#{sheet_index}"] == true
		end
	end
	distinct_aspects = distinct_aspects.keys

	report_data = TieredReportData.new(distinct_aspects)
	report_data.setCollectAuditedCount(collect_audited_count)
	report_data.setCollectAuditedSize(collect_audited_sizes)
	report_data.setCollectFileSize(collect_file_sizes)
	report_data.setCollectDigestInputSize(collect_digest_input_sizes)
	report_data.setCollectIsCorrupted(collect_corrupted_count)
	report_data.setCollectIsEncrypted(collect_encrypted_count)
	report_data.setCollectIsDeleted(collect_deleted_count)
	
	last_progress = Time.now

	ProgressDialog.forBlock do |pd|
		pd.setTitle("Tiered Report")
		pd.setAbortButtonVisible(true)

		# Connect dialog abort to scan abort
		pd.onAbort do
			report_data.requestAbort
		end

		report_data.onProgressUpdated  do |count,total|
			if (Time.now - last_progress) > 0.25
				pd.setSubProgress(count)
				pd.setSubStatus("#{count}/#{total}")
				last_progress = Time.now
			end
		end

		report_data.onProgressMessage do |message|
			pd.logMessage(message)
		end

		if date_range_filter_batches
			pd.logMessage("Batch Load Date Range:")
			pd.logMessage("\tMin: #{batch_load_min_date}")
			pd.logMessage("\tMax: #{batch_load_max_date}")
		end

		# Generate report sheet info objects, needed for scan time and report time
		report_sheet_infos = []
		report_sheets.times do |sheet_index|
			sheet_index += 1
			if values["generate_sheet_#{sheet_index}"]
				sheet_name = values["sheet_name_#{sheet_index}"]
				sheet_aspects = values["sheet_aspects_#{sheet_index}"]

				report_sheet_info = ReportSheetInfo.new(sheet_name,sheet_aspects)
				report_sheet_info.setReportAuditedCount(values["report_audited_count_#{sheet_index}"])
				report_sheet_info.setReportAuditedSize(values["report_audited_size_#{sheet_index}"])
				report_sheet_info.setReportFileSize(values["report_file_size_#{sheet_index}"])
				report_sheet_info.setReportDigestInputSize(values["report_digest_input_size_#{sheet_index}"])
				report_sheet_info.setReportCorruptedCount(values["report_corrupted_#{sheet_index}"])
				report_sheet_info.setReportEncryptedCount(values["report_encrypted_#{sheet_index}"])
				report_sheet_info.setReportDeletedCount(values["report_deleted_#{sheet_index}"])
				report_sheet_info.setSortLastTierByItemCount(values["last_tier_count_sorted_#{sheet_index}"])
				report_sheet_info.setIncludeCategoryHeaderRows(values["include_category_headers_#{sheet_index}"])
				report_sheet_info.setUseSparseColumns(values["use_sparse_columns_#{sheet_index}"])
				report_sheet_info.setSubScopeQuery(values["sub_scope_query_#{sheet_index}"])
				sheet_size_unit = values["size_unit_#{sheet_index}"]
				case sheet_size_unit
				when "Bytes"
					report_sheet_info.setSizeUnit(FileSizeUnit::BYTES)
				when "Dynamic"
					report_sheet_info.setSizeUnit(FileSizeUnit::DYNAMIC)
				else
					report_sheet_info.setSizeUnit(FileSizeUnit::GIGABYTES)
				end
				report_sheet_infos << report_sheet_info
			end
		end

		pd.logMessage("Scope Query: #{scope_query}")
		errored_case_paths = []
		if values["use_case_paths"]
			pd.setMainProgress(0,values["case_paths"].size)
			values["case_paths"].each_with_index do |case_path,case_path_index|
				case_opened = false
				if !$current_case.nil? && $current_case.getLocation.getPath.downcase == case_path.strip.downcase
					pd.logMessage("\n==== Using current case for path: #{case_path} ====")
					$current_reporting_case = $current_case
					case_opened = true
				else
					begin
						pd.logMessage("\n==== Opening Case: #{case_path} ====")
						$current_reporting_case = $utilities.getCaseFactory.open(case_path,{:migrate=>values["allow_migration"]})
						case_opened = true
					rescue Exception => exc
						errored_case_paths << { :path => case_path, :message => exc.message }
						pd.logMessage("An error occurred while opening the case:\n#{exc.message}")
						case_opened = false
					end
				end
				if case_opened
					pd.setMainStatusAndLogIt("Scanning Case: #{$current_reporting_case.getName}")
					items = []
					if date_range_filter_batches
						pd.logMessage("Determining Batch Loads in Date Range...")
						batch_loads = $current_reporting_case.getBatchLoads
						range_loads = batch_loads.select{|bl| !bl.getLoaded.isBefore(batch_load_min_date) && ! bl.getLoaded.isAfter(batch_load_max_date)}
						if range_loads.size < 1
							pd.logMessage("No batch loads in date range")
						else
							pd.logMessage("Batches in Date Range: #{range_loads.size}")
							range_load_ids = range_loads.map{|bl|bl.getBatchId}
							modified_scope_query = []
							modified_scope_query << "batch-load-guid:(#{range_load_ids.join(" OR ")})"
							modified_scope_query << "(#{scope_query})" if !scope_query.empty?
							modified_scope_query = modified_scope_query.join(" AND ")
							items = $current_reporting_case.search(modified_scope_query)
						end
					else
						items = $current_reporting_case.search(scope_query)
					end

					if items.size > 0
						if !values["report_excluded"]
							pd.logMessage("Filtering excluded items out...")
							excluded_items = $current_reporting_case.searchUnsorted("has-exclusion:1")
							items = $utilities.getItemUtility.difference(items,excluded_items)
						end

						pd.setSubProgress(0,items.size)
						report_data.scanItems($current_reporting_case,$utilities,items,report_sheet_infos)
					else
						pd.logMessage("No items to report on")
					end

					if !$current_case.nil? && $current_case.getLocation.getPath.downcase == $current_reporting_case.getLocation.getPath.strip.downcase
						#Leave $current_case open
					else
						$current_reporting_case.close
					end
				end
				pd.setMainProgress(case_path_index+1)
			end
		else
			$current_reporting_case = $current_case
			pd.setMainProgress(0,1)
			pd.setMainStatusAndLogIt("Scanning Current Case: #{$current_reporting_case.getName}")
			items = []
			if date_range_filter_batches
				pd.logMessage("Determining Batch Loads in Date Range...")
				batch_loads = $current_case.getBatchLoads
				range_loads = batch_loads.select{|bl| !bl.getLoaded.isBefore(batch_load_min_date) && ! bl.getLoaded.isAfter(batch_load_max_date)}
				if range_loads.size < 1
					pd.logMessage("No batch loads in date range")
				else
					pd.logMessage("Batches in Date Range: #{range_loads.size}")
					range_load_ids = range_loads.map{|bl|bl.getBatchId}
					modified_scope_query = []
					modified_scope_query << "batch-load-guid:(#{range_load_ids.join(" OR ")})"
					modified_scope_query << "(#{scope_query})" if !scope_query.empty?
					modified_scope_query = modified_scope_query.join(" AND ")
					items = $current_reporting_case.search(modified_scope_query)
				end
			else
				items = $current_reporting_case.search(scope_query)
			end

			if !values["report_excluded"]
				pd.logMessage("Filtering excluded items out...")
				excluded_items = $current_reporting_case.searchUnsorted("has-exclusion:1")
				items = $utilities.getItemUtility.difference(items,excluded_items)
			end

			pd.setSubProgress(0,items.size)
			report_data.scanItems($current_reporting_case,$utilities,items,report_sheet_infos)
			pd.setMainProgress(1)
		end

		if pd.abortWasRequested
			pd.setMainStatus("User Aborted")
			pd.logMessage("User Aborted, no report will be generated")
			pd.setSubStatus("")
			pd.setMainProgress(1,1)
			pd.setSubProgress(1,1)
		elsif !report_data.dataWasCollected
			pd.setMainStatusAndLogIt("No report data was collected")

			pd.setSubStatus("")
			pd.setMainProgress(1,1)
			pd.setSubProgress(1,1)
		else
			pd.setMainStatusAndLogIt("\n==== Generating Report ====")
			
			ReportGenerator.setUseSIUnits(values["use_si_units"])
			ReportGenerator.generateReport(report_data,java.io.File.new(values["report_file"]),report_sheet_infos) do |sheetName,tierPath|
				pd.setSubStatus("#{sheetName}: #{tierPath}")
			end

			pd.setMainStatusAndLogIt("Completed in #{Time.at(Time.now - start_time).gmtime.strftime("%H:%M:%S")}")
			pd.setCompleted

			if values["open_report"]
				java.awt.Desktop.getDesktop.open(java.io.File.new(values["report_file"]))
			end
		end

		if errored_case_paths.size > 0
			pd.logMessage("There were #{errored_case_paths.size} cases that had errors opening:")
			errored_case_paths.each do |error_info|
				pd.logMessage("Path: #{error_info[:path]}")
				pd.logMessage("\tMessage: #{error_info[:message]}")
			end
		end
	end
end