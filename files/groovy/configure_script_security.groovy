/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

ScriptApproval scriptApproval = ScriptApproval.get()
String[] currentApprovedSignatures = scriptApproval.getApprovedSignatures()

// parse variables passed from Ansible
signaturesPresent = new JsonSlurper().parseText('$signaturesPresent')
signaturesAbsent = new JsonSlurper().parseText('$signaturesAbsent')

// defaulting list variables
if (signaturesPresent == null || !signaturesPresent instanceof List) {
  signaturesPresent = []
}

if (signaturesAbsent == null || !signaturesAbsent instanceof List) {
  signaturesAbsent = []
}

List<String> changedSignaturesPresent = new ArrayList<String>()
List<String> changedSignaturesAbsent = new ArrayList<String>()

List<String> changedClasspathsPresent = new ArrayList<String>()
List<String> changedClasspathsAbsent = new ArrayList<String>()

// make sure no absent signatures are in present signatures
for (String absentSignature in signaturesAbsent) {
  if (signaturesPresent.contains(absentSignature)) {
    println("absentSignature '"+absentSignature+"' is in signaturesPresent, removing");
    signaturesPresent.remove(absentSignature)
  }
}


// clear approvals
scriptApproval.clearApprovedSignatures()

// add all script approvals that are not in the absent list
for (String currentApprovedSignature in currentApprovedSignatures) {
  // trim
  currentApprovedSignature = currentApprovedSignature.trim()
  Boolean doRemove = signaturesAbsent.contains(currentApprovedSignature)
  if (doRemove) {
    changedSignaturesAbsent.push(currentApprovedSignature)
  } else {
    scriptApproval.approveSignature(currentApprovedSignature)
  }
}

for (String presentSignature in signaturesPresent) {
  // trim
  presentSignature = presentSignature.trim()
  Boolean alreadyApproved = currentApprovedSignatures.contains(presentSignature)
  if (!alreadyApproved) {
    scriptApproval.approveSignature(presentSignature)
    changedSignaturesPresent.push(presentSignature)
  }
}

// save changes
scriptApproval.save()

def json = JsonOutput.toJson([
  changed: changedSignaturesPresent.size() > 0 ||
    changedSignaturesAbsent.size() > 0 ||
    changedClasspathsPresent.size() > 0 ||
    changedClasspathsAbsent.size() > 0,
  'present' : [
    'signatures' : changedSignaturesPresent
  ],
  'absent' : [
    'signatures' : changedSignaturesAbsent
  ]
])

//return json.toString()
return json.bytes.encodeBase64().toString()