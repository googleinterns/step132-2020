// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Displays registration information on the page
 */
function displayRegistrationInfo() {
    var generalInfo = document.getElementById('general-info');
    var tutorInfo = document.getElementById('tutor-info');
    generalInfo.style.display = 'block';

    // Display extra information to fill if user is a tutor
    if (document.getElementById('tutor').checked) {
        tutorInfo.style.display = 'block';
    } else {   // User may have clicked tutor then switched back to student, in that case hide tutor information
        tutorInfo.style.display = 'none';
    }
}
