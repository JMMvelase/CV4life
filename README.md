1. User Authentication & Roles
Firebase Authentication is used to manage user sign‑up, sign‑in, and sign‑out.

Once signed in, each user is classified as either a Student or a Recruiter, and the app directs them to the corresponding dashboard (StudentDashboardActivity or RecruiterDashboardActivity).

2. Student (Job‑Seeker) Features
a. Browse & Apply for Jobs
View Available Jobs: Opens a list of all “open” job postings (JobsActivity).

My Applications: Lets students see which jobs they’ve applied for (ApplicationsActivity).

b. Profile & CV Management
Edit CV: A built‑in editor (CVEditorActivity) where students can build or tweak their résumé.

Edit Profile: Manage personal details (name, contact info, photo) in ProfileActivity.

c. Chatbot Assistance
Chatbot: Launches a chat interface (viviActivity)—could be a helper that answers FAQs, suggests jobs, or walks students through the application process.

d. Settings & Logout
Settings: Adjust app preferences (notifications, privacy, theme) via SettingsActivity.

Logout: Signs out and clears the navigation stack, sending the user back to LoginActivity.

3. Recruiter (Job‑Poster) Features
a. Post New Jobs
Post Job Dialog: A pop‑up form (dialog_post_job.xml) where recruiters enter:

Job title, description, requirements

Select a sector from a dropdown

On submission, the job is saved to Firestore with fields like recruiterId, createdAt, and status: "open".

b. Manage Applications
View Applications: Opens ViewApplicationsActivity, showing candidates who’ve applied to the recruiter’s postings.

c. Profile Management
Edit Profile: (Placeholder) would allow recruiters to manage company details, logos, contact info, etc.

d. Logout
Exactly the same sign‑out logic as the student side: clear session, clear backstack, return to login.

4. Data & Persistence
Firestore Schema (implied):

jobs collection

Documents with fields: title, description, requirements, sector, recruiterId, createdAt, status

applications collection (accessed in ApplicationsActivity/ViewApplicationsActivity)

Likely stores links between jobId, studentId, application date, and maybe application status.

Real‑Time Updates: Using Firestore means any changes (new posts, new applications) can reflect immediately in the UI.

5. UX Flow
Login / Sign‑Up → FirebaseAuth

Role Check → Launch Student vs. Recruiter Dashboard

Dashboard Cards → Navigate to specific tasks (browse, post, edit, chat)

Dialogs & Editors → In‑app forms for creating or updating data

Data Saves → Firestore reads/writes power dynamic lists of jobs and applications

Logout → Safe sign‑out and return to login screen

In Short
This is a two‑sided marketplace mobile app, matching students looking for work or internships with recruiters posting opportunities. It provides all the essential features: authentication, job browsing and posting, application tracking, in‑app résumé/profile editing, and a chatbot for assistance—all backed by Firebase’s Authentication and Firestore services.
