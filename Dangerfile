# Check PR size
warn("Big PR! Consider breaking it up into smaller ones.") if git.lines_of_code > 500

# Warn when there's no PR description
warn("Please provide a PR description") if github.pr_body.length < 10

# Check for modified test files
has_test_changes = git.modified_files.any? { |file| file.include?("Test.kt") }
has_source_changes = git.modified_files.any? { |file| file.include?("src/main") }

if has_source_changes && !has_test_changes
  warn("You modified source files but didn't add or modify any tests. Please consider adding tests.")
end

# Check for TODO/FIXME comments
for file in git.modified_files
  next unless file.end_with?(".kt", ".java")
  
  diff = git.diff_for_file(file)
  if diff && diff.patch.include?("TODO") || diff.patch.include?("FIXME")
    warn("TODO/FIXME comment added in #{file}")
  end
end

# Congratulate on adding tests
message("Great job adding tests! 🎉") if has_test_changes