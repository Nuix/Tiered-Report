Tiered Report
==============

![image](https://user-images.githubusercontent.com/11775738/40689923-562ad8e6-6359-11e8-8188-be0ca3b246f4.png)

![image](https://user-images.githubusercontent.com/11775738/40689930-5bd97ff4-6359-11e8-88f4-feb7af4553fe.png)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0) ![This script was last tested in Nuix 8.0](https://img.shields.io/badge/Script%20Tested%20in%20Nuix-8.0-green.svg)

View the GitHub project [here](https://github.com/Nuix/Tiered-Report) or download the latest release [here](https://github.com/Nuix/Tiered-Report/releases).

# Overview

**Written By:** Jason Wells

This script allows you to define and generate reports on data ingested into one or more Nuix cases.  The report is generated as Excel XLSX work book with one or more work sheets.  For each work sheet you select one or more [Item Aspects](#item-aspects), which represent information which can be captured about an item such as:

- Custodian
- Item Kind
- Tags
- etc.

Each report sheet is then generated in a tiered manner.

For example if I were to specify a sheet in the report with the following aspects:

- Evidence Name
- Item Kind
- Irregular Categories

I would get a report sheet which is first broken down by Evidence Name and then within that evidence further broken down by Item Kind and then finally broken down by Irregular Item Category.

For each tier the total number of items is reported as well as optionally things like total audited size and total file size.

## Cloning this Repository

This script relies on code from [Nx](https://github.com/Nuix/Nx) to present a settings dialog and progress dialog.  This JAR file is not included in the repository (although it is included in release downloads).  If you clone this repository, you will also want to obtain a copy of Nx.jar by either:
1. Building it from [the source](https://github.com/Nuix/Nx)
2. Downloading an already built JAR file from the [Nx releases](https://github.com/Nuix/Nx/releases)

Once you have a copy of Nx.jar, make sure to include it in the same directory as the script.

# Getting Started

## Setup

Begin by downloading the latest release of this code.  Extract the contents of the archive into your Nuix scripts directory.  In Windows the script directory is likely going to be either of the following:

- `%appdata%\Nuix\Scripts` - User level script directory
- `%programdata%\Nuix\Scripts` - System level script directory

## Item Aspects

These are aspects of items which you would like reported.  Each selected aspect represents a tier in the report.

| Name | Description |
|------|-------------|
| Case Location | Categorizes items by the directory path of the case they came from. |
| Case Name | Categorizes items by the name of the case they came from. |
| Custodian | Categorizes items by the name of the custodian assigned to them. Items in which no custodian has been assigned will be categorized as `No Custodian`. |
| Is Deleted | Categorizes items by `TRUE` or `FALSE` based on whether the item was marked as deleted when ingested. |
| Is Encrypted | Categorizes items by `TRUE` or `FALSE` based on whether the item was marked as encrypted when ingested. |
| Evidence Name | Categorizes items by the name of the evidence under which they were ingested. |
| Exclusion Name | Categorizes excluded items by the name of the exclusion to which they belong. Items which are not excluded, will be categorized as `Not Excluded`. |
| Irregular Categories | Categorizes items by the irregular categories they belong to. |
| Item Category | Categorizes items based on item category, as returned by Item.getItemCategory. Will categorize items in which Nuix has no category defined as `No Category` |
| Item Date | Categorizes items based on their item date, formatted like `YYYYMMDD` (ex: 19820602). If for some reason an item does not have an item date, it will be categorized as `No Date`. |
| Item Date Month | Categorizes items by month of the year (01-12) based on their item date. If an item has no item date, it will be categorized as `No Date`. |
| Item Date Year | Categorizes items by the year (2001, 2002, etc) based on their item date. If an item has no item date, it will be categorized as `No Date`. |
| Item Date Year/Month | Categorizes items by the year and month of the year (2001/01, 2001/02, etc) based on their item date. If an item has no item date, it will be categorized as `No Date`. |
| Top Level Item Date | Categorizes items based on their top level item's item date, formatted like `YYYYMMDD` (ex: 19820602). If the top level item has no item date, or the item has no top level item, it will be categorized as `No Top Level Item Date`. |
| Top Level Item Date Month | Categorizes items by month of the year (01-12) based on their top level item's item date. If the top level item has no item date, or the item has no top level item, it will be categorized as `No Top Level Item Date`. |
| Top Level Item Date Year | Categorizes items by the year (2001, 2002, etc) based on their top level item's item date. If the top level item has no item date, or the item has no top level item, it will be categorized as `No Top Level Item Date`. |
| Top Level Item Date Year/Month | Categorizes items by the year and month of the year (2001/01, 2001/02, etc) based on their top level item's item date. If the top level item has no item date, or the item has no top level item, it will be categorized as `No Top Level Item Date`. |
| Item Kind | Categorizes items based on the name of the item kind assigned by Nuix (email, image, etc). |
| Top Level Item Kind | Categorizes items based on the name of the item kind of an item's top level item (`email`, `image`, etc). |
| Item Type | Categorizes items based on the type name assigned by Nuix (`Microsoft Outlook Note`, `Tagged Image Format File`, etc). |
| Top Level Item Type | Categorizes items based on the type name of an item's top level item (`Microsoft Outlook Note`, `Tagged Image Format File`, etc). |
| Mime Type | Categorizes items based on the mimetype assigned by Nuix (`application/vnd.ms-outlook-note`, `image/tiff`, etc). |
| Original Extension | Categorizes items based the original extension value captured by Nuix. Items without an extension will be classified as `No Extension`. |
| Corrected Extension | Categorizes items based the corrected extension value in Nuix (based on detected item type). Items without an extension will be classified as `No Extension`. |
| Language | Categorizes items based on the language detected by Nuix (`eng`, `fra`, etc). If Nuix has no detected language recorded for an item, it will be categorized as `No Language Detected`. |
| Material Status | Categorizes items by `Material` or `Immaterial` based on whether the item was marked as material (audited) when ingested. |
| Production Set | Categorizes items based on the production sets they may belong to. |
| Property Names | Categorizes items based on the names of the metadata properties present on each item. |
| Recipient Domains | Categorizes items based on the email address domains present in the communication fields `To`, `CC` and `BCC`. If a domain is unable to be parsed from an address it will be recorded as `No Domain`. If the item does not have a communication object (item which does not have recipient fields) then it will be categorized as `Non Communication`. |
| Recipient Addresses | Categorizes items based on the email addresses present in the communication fields `To`, `CC` and `BCC`. If the item does not have a communication (item which does not have recipient fields) then it will be categorized as `Non Communication`. |
| Sender Addresses | Categorizes items based on the email addresses present in the communication field `From`. If the item does not have a communication then it will be categorized as `Non Communication`. |
| Sender Domains | Categorizes items based on the email address domains present in the field `From`. If a domain is unable to be parsed from an address it will be recorded as `No Domain`. If the item does not have a communication (item which does not have recipient fields) then it will be categorized as `Non Communication`. |
| To Domains | Categorizes items based on the email address domains present in the communication field `To`. If a domain is unable to be parsed from an address it will be recorded as `No Domain`. If the item does not have a communication object then it will be categorized as `Non Communication`. |
| CC Domains | Categorizes items based on the email address domains present in the communication field `CC`. If a domain is unable to be parsed from an address it will be recorded as `No Domain`. If the item does not have a communication object then it will be categorized as `Non Communication`. |
| BCC Domains | Categorizes items based on the email address domains present in the communication field `BCC`. If a domain is unable to be parsed from an address it will be recorded as `No Domain`. If the item does not have a communication object then it will be categorized as `Non Communication`. |
| Tags | Categorizes items based on the tags applied to each item. Items which have no tags applied will be categorized as `No Tags` |
| Physical File Name | Categorizes items based name of the ancestor item which is responds to the query `flag:physical_file`. This could be used in some instances to categorize items based on things like the name of the originating PST they came from. If a physical file ancestor is unable to be resolved for an item, it will be categorized as `No Physical File Ancestor`.  **Important Note:** This aspect performs heavy traversal over the item structure and may degrade reporting performance. This aspect also has potential to generate very large reports depending on the source data. Use with care! |
| Named Entity | Categorizes items based on the named entity types located in an item during ingestion. |
| Item Set | Categorizes items based on the names of the items sets which they are a member of. |
| Digest Lists | Categorizes items based on the names of the digest lists which they are a member of. |
| Terms | Categorizes items based on a list of terms which they are responsive to. |
| Batch Load ID | Categorizes items based on the GUID of the Batch Load to which they belong. |
| Batch Load Date | Categorizes items based on the loaded date of the Batch Load to which they belong. |
| Global Item Set Duplicate Status | Categorizes items based on whether they are a duplicate or original in any item set. If a given item is not present in any item set this will classify the item as `No Item Set`. |
| Item Set Name and Duplicate Status | Categorizes items by the name of each item set they are present in and whether they are a duplicate or original in that item set. For example if an item is original in Item Set A, but a duplicate in Item Set B then this will categorize the item as Item Set A - Original and Item Set B - Duplicate. If this is nested below the aspect Item Set it will not show duplicates/originals for only that item set, but instead any item sets which contains the same items present in the given item set. |


# License

```
Copyright 2019 Nuix

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```